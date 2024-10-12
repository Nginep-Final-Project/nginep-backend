package com.example.nginep.payments.service.impl;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.cloudinary.dto.CloudinaryUploadResponseDto;
import com.example.nginep.cloudinary.service.CloudinaryService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.midtrans.service.MidtransService;
import com.example.nginep.payments.dto.UploadProofOfPaymentDto;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import com.example.nginep.payments.repository.PaymentRepository;
import com.example.nginep.payments.service.PaymentService;
import com.example.nginep.payments.tasks.CancelUnconfirmedManualPaymentTask;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final CloudinaryService cloudinaryService;
    private final MidtransService midtransService;
    private final CancelUnconfirmedManualPaymentTask cancelUnconfirmedManualPaymentTask;
    private final TaskScheduler taskScheduler;
    private final BookingService bookingService;

    private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("jpg", "png");
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private static final int MAX_PAYMENT_ATTEMPTS = 3;

    public PaymentServiceImpl(PaymentRepository paymentRepository, CloudinaryService cloudinaryService, MidtransService midtransService, @Lazy CancelUnconfirmedManualPaymentTask cancelUnconfirmedManualPaymentTask, TaskScheduler taskScheduler, @Lazy BookingService bookingService) {
        this.paymentRepository = paymentRepository;
        this.cloudinaryService = cloudinaryService;
        this.midtransService = midtransService;
        this.taskScheduler = taskScheduler;
        this.cancelUnconfirmedManualPaymentTask = cancelUnconfirmedManualPaymentTask;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public Map<String, Object> createPayment(Booking booking, BigDecimal amount, PaymentType paymentType, String bank) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);
        payment.setStatus(PaymentStatus.PENDING_PAYMENT);
        payment.setExpiryTime(Instant.now().plus(60, ChronoUnit.MINUTES));
        payment.setAttempts(0);

        payment = paymentRepository.save(payment);

        Map<String, Object> result = new HashMap<>();
        result.put("payment", payment);

        if (paymentType == PaymentType.AUTOMATIC_PAYMENT) {
            try {
                JSONObject chargeResponse = midtransService.createBankTransferCharge(
                        payment.getId().toString(),
                        payment.getAmount().longValue(),
                        bank
                );

                String transactionStatus = chargeResponse.getString("transaction_status");
                if (!"pending".equals(transactionStatus)) {
                    throw new ApplicationException("Unexpected transaction status: " + transactionStatus);
                }

                updatePaymentWithMidtransResponse(payment, chargeResponse, bank);
                paymentRepository.save(payment);

                result.put("midtransResponse", chargeResponse.toMap());
            } catch (Exception e) {
                throw new ApplicationException("Failed to create Midtrans charge: " + e.getMessage());
            }
        }

        return result;
    }

    private void updatePaymentWithMidtransResponse(Payment payment, JSONObject chargeResponse, String bank) {
        payment.setSpecificPaymentType(bank);

        switch (bank.toLowerCase()) {
            case "bca":
            case "bni":
            case "bri":
                JSONObject vaNumbers = chargeResponse.getJSONArray("va_numbers").getJSONObject(0);
                payment.setVaNumber(vaNumbers.getString("va_number"));
                break;
            case "permata":
                payment.setVaNumber(chargeResponse.getString("permata_va_number"));
                break;
            case "mandiri":
                payment.setBillKey(chargeResponse.getString("bill_key"));
                payment.setBillerCode(chargeResponse.getString("biller_code"));
                break;
            case "e-wallet/qris":
                JSONObject qrisUrl = chargeResponse.getJSONArray("actions").getJSONObject(0);
                payment.setQrisUrl(qrisUrl.getString("url"));
                break;
            default:
                throw new ApplicationException("Unsupported bank: " + bank);
        }
    }

    @Override
    @Transactional
    public Payment uploadProofOfPayment(UploadProofOfPaymentDto uploadProofOfPaymentDTO) {
        Payment payment = findPaymentById(uploadProofOfPaymentDTO.getPaymentId());

        if (payment.getPaymentType() != PaymentType.MANUAL_PAYMENT) {
            throw new ApplicationException("Proof of payment can only be uploaded for manual payments");
        }

        if (payment.getStatus() != PaymentStatus.PENDING_PAYMENT && payment.getStatus() != PaymentStatus.REJECTED) {
            throw new ApplicationException("Payment proof can only be uploaded for pending or rejected payments");
        }

        if (Instant.now().isAfter(payment.getExpiryTime())) {
            throw new ApplicationException("Payment has expired. Please create a new booking.");
        }

        if (payment.getAttempts() >= MAX_PAYMENT_ATTEMPTS) {
            throw new ApplicationException("Maximum payment attempts reached. Please create a new booking.");
        }

        MultipartFile file = uploadProofOfPaymentDTO.getProofOfPayment();
        validateFile(file);

        CloudinaryUploadResponseDto uploadResult = cloudinaryService.uploadImage(uploadProofOfPaymentDTO.getProofOfPayment());
        payment.setProofOfPayment(uploadResult.getUrl());
        payment.setStatus(PaymentStatus.AWAITING_CONFIRMATION);
        payment.setPaidAt(Instant.now());
        payment.setAttempts(payment.getAttempts() + 1);

        scheduleUnconfirmedManualPaymentCancellation(payment.getId());

        return paymentRepository.save(payment);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApplicationException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ApplicationException("Invalid file format. Only JPG and PNG files are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApplicationException("File size exceeds the maximum limit of 1MB");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            throw new ApplicationException("Filename is null");
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1);
        }
        throw new ApplicationException("Unable to determine file extension");
    }

    private void scheduleUnconfirmedManualPaymentCancellation(Long paymentId) {
        cancelUnconfirmedManualPaymentTask.setPaymentId(paymentId);
        taskScheduler.schedule(cancelUnconfirmedManualPaymentTask, Instant.now().plus(24, ChronoUnit.HOURS));
    }

    @Override
    @Transactional
    public void cancelUnconfirmedManualPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        Booking booking = payment.getBooking();

        if (payment.getStatus() == PaymentStatus.AWAITING_CONFIRMATION &&
                (booking.getStatus() == BookingStatus.PENDING_PAYMENT || booking.getStatus() == BookingStatus.AWAITING_CONFIRMATION)) {
            booking.setStatus(BookingStatus.CANCELLED);
            // How to refund 101

            paymentRepository.save(payment);
        }
    }

    @Override
    @Transactional
    public Payment confirmManualPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.AWAITING_CONFIRMATION) {
            throw new ApplicationException("Payment is not in a state to be confirmed");
        }

        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.getBooking().setStatus(BookingStatus.AWAITING_CONFIRMATION);
        Payment confirmedPayment = paymentRepository.save(payment);

        bookingService.scheduleUnconfirmedBookingCancellation(confirmedPayment.getBooking().getId());

        return confirmedPayment;
    }

    @Override
    @Transactional
    public Payment rejectPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.AWAITING_CONFIRMATION) {
            throw new ApplicationException("Only payments awaiting confirmation can be rejected");
        }

        if (payment.getAttempts() >= MAX_PAYMENT_ATTEMPTS) {
            payment.setStatus(PaymentStatus.CANCELLED);
        } else {
            payment.setPaidAt(null);
            payment.setStatus(PaymentStatus.REJECTED);
            payment.setExpiryTime(Instant.now().plus(1, ChronoUnit.HOURS));
        }

        return paymentRepository.save(payment);
    }

    @Override
    public PaymentStatus getPaymentStatusForBooking(Long bookingId) {
        Payment payment = findPaymentByBookingId(bookingId);
        return payment.getStatus();
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found with id: " + paymentId));
    }

    private Payment findPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElseThrow(() -> new NotFoundException("Payment not found for booking id: " + bookingId));
    }

    @Override
    @Transactional
    public Map<String, Object> getMidtransDetails(String orderId) {
        Payment payment = findPaymentById(Long.parseLong(orderId));
        Map<String, Object> details = new HashMap<>();

        try {
            JSONObject midtransStatus = midtransService.getTransactionStatus(payment.getId().toString());
            details.put("midtransDetails", midtransStatus.toMap());
        } catch (Exception e) {
            throw new ApplicationException("Failed to fetch Midtrans data: " + e.getMessage());
        }

        return details;
    }

    @Override
    @Transactional
    public Payment updatePaymentStatusMidtrans(String orderId, String transactionStatus, String fraudStatus) {
        Payment payment = findPaymentById(Long.valueOf(orderId));

        switch (transactionStatus) {
            case "capture":
                if ("challenge".equals(fraudStatus)) {
                    payment.setStatus(PaymentStatus.AWAITING_CONFIRMATION);
                } else if ("accept".equals(fraudStatus)) {
                    payment.setStatus(PaymentStatus.CONFIRMED);
                }
                break;
            case "settlement":
                payment.setStatus(PaymentStatus.CONFIRMED);
                break;
            case "deny":
            case "cancel":
            case "expire":
                payment.setStatus(PaymentStatus.CANCELLED);
                break;
            case "pending":
                payment.setStatus(PaymentStatus.PENDING_PAYMENT);
                break;
            default:
                throw new ApplicationException("Unhandled transaction status: " + transactionStatus);
        }
        return paymentRepository.save(payment);
    }

}