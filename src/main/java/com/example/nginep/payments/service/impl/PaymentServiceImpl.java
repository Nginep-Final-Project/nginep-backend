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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
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
    private final JavaMailSender javaMailSender;

    private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("jpg", "png");
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private static final int MAX_PAYMENT_ATTEMPTS = 3;

    public PaymentServiceImpl(PaymentRepository paymentRepository, CloudinaryService cloudinaryService, MidtransService midtransService, @Lazy CancelUnconfirmedManualPaymentTask cancelUnconfirmedManualPaymentTask, TaskScheduler taskScheduler, @Lazy BookingService bookingService, JavaMailSender javaMailSender) {
        this.paymentRepository = paymentRepository;
        this.cloudinaryService = cloudinaryService;
        this.midtransService = midtransService;
        this.taskScheduler = taskScheduler;
        this.cancelUnconfirmedManualPaymentTask = cancelUnconfirmedManualPaymentTask;
        this.bookingService = bookingService;
        this.javaMailSender = javaMailSender;
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
        payment.getBooking().setStatus(BookingStatus.AWAITING_CONFIRMATION);
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
        payment.getBooking().setStatus(BookingStatus.CONFIRMED);
        Payment confirmedPayment = paymentRepository.save(payment);

        sendPaymentConfirmationEmail(confirmedPayment);
        bookingService.scheduleCheckInReminder(confirmedPayment.getBooking());

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
            payment.getBooking().setStatus(BookingStatus.CANCELLED);
        } else {
            payment.setPaidAt(null);
            payment.setStatus(PaymentStatus.REJECTED);
            payment.setExpiryTime(Instant.now().plus(1, ChronoUnit.HOURS));
        }

        payment.setStatus(PaymentStatus.REJECTED);
        payment.getBooking().setStatus(BookingStatus.PENDING_PAYMENT);

        Payment rejectedPayment = paymentRepository.save(payment);

        sendPaymentRejectionEmail(rejectedPayment);

        return rejectedPayment;
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
    public Payment updatePaymentStatusMidtrans(Payment payment, String transactionStatus, String fraudStatus) {
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

    @Override
    public Payment findPaymentByOrderId(String orderId) {
        return paymentRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new NotFoundException("Payment not found for order ID: " + orderId));
    }

    @Override
    public void sendPaymentConfirmationEmail(Payment payment) {
        try {
            String toAddress = payment.getBooking().getUser().getEmail();
            String fromAddress = "nginepproject@gmail.com";
            String senderName = "Nginep";
            String subject = "Payment Confirmed for Your Booking at " +
                    payment.getBooking().getRoom().getProperty().getPropertyName();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(fromAddress, senderName));
            helper.setTo(toAddress);
            helper.setSubject(subject);

            String content = "Dear " + payment.getBooking().getUser().getFullName() + ",<br><br>"
                    + "Your payment has been confirmed for your upcoming stay at "
                    + payment.getBooking().getRoom().getProperty().getPropertyName() + ".<br><br>"
                    + "Booking Details:<br>"
                    + "Property: " + payment.getBooking().getRoom().getProperty().getPropertyName() + "<br>"
                    + "Room: " + payment.getBooking().getRoom().getName() + "<br>"
                    + "Address: " + payment.getBooking().getRoom().getProperty().getPropertyAddress() + ", "
                    + payment.getBooking().getRoom().getProperty().getPropertyCity() + "<br>"
                    + "Host: " + payment.getBooking().getRoom().getProperty().getUser().getFullName() + "<br>"
                    + "Number of Guests: " + payment.getBooking().getNumGuests() + "<br><br>"
                    + "Check-in time: " + payment.getBooking().getRoom().getProperty().getUser().getCheckinTime() + "<br>"
                    + "Check-out time: " + payment.getBooking().getRoom().getProperty().getUser().getCheckoutTime() + "<br>"
                    + "Check-in date: " + payment.getBooking().getCheckInDate() + "<br>"
                    + "Check-out date: " + payment.getBooking().getCheckOutDate() + "<br><br>"
                    + "Payment Details:<br>"
                    + "Amount Paid: Rp " + payment.getAmount().toString() + "<br>"
                    + "Payment Method: " + payment.getPaymentType() + "<br>"
                    + "Payment Date: " + payment.getPaidAt() + " GMT+0 / UTC+0 " + "<br>"
                    + "Transaction ID: " + payment.getId() + "<br><br>"
                    + "We're excited to have you as our guest!<br><br>"
                    + "Best regards,<br>"
                    + "Nginep Team";

            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("Successfully sent payment confirmation email to {}", toAddress);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send payment confirmation email", e);
        }
    }

    private void sendPaymentRejectionEmail(Payment payment) {
        try {
            String toAddress = payment.getBooking().getUser().getEmail();
            String fromAddress = "nginepproject@gmail.com";
            String senderName = "Nginep";
            String subject = "Payment Rejected - Action Required for Your Booking at " +
                    payment.getBooking().getRoom().getProperty().getPropertyName();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(fromAddress, senderName));
            helper.setTo(toAddress);
            helper.setSubject(subject);

            String content = "Dear " + payment.getBooking().getUser().getFullName() + ",<br><br>"
                    + "We regret to inform you that your payment proof for the booking at "
                    + payment.getBooking().getRoom().getProperty().getPropertyName() + " has been rejected.<br><br>"
                    + "Booking Details:<br>"
                    + "Property: " + payment.getBooking().getRoom().getProperty().getPropertyName() + "<br>"
                    + "Room: " + payment.getBooking().getRoom().getName() + "<br>"
                    + "Address: " + payment.getBooking().getRoom().getProperty().getPropertyAddress() + ", "
                    + payment.getBooking().getRoom().getProperty().getPropertyCity() + "<br>"
                    + "Host: " + payment.getBooking().getRoom().getProperty().getUser().getFullName() + "<br>"
                    + "Number of Guests: " + payment.getBooking().getNumGuests() + "<br><br>"
                    + "Check-in date: " + payment.getBooking().getCheckInDate() + "<br>"
                    + "Check-out date: " + payment.getBooking().getCheckOutDate() + "<br><br>"
                    + "Amount to be Paid: Rp " + payment.getAmount().toString() + "<br><br>"
                    + "Action Required:<br>"
                    + "Please submit a new payment proof through the following steps:<br>"
                    + "1. Log in to your Nginep account<br>"
                    + "2. Go to 'Reservations'<br>"
                    + "3. Find the booking on the 'Awaiting Your Payment' section and click on 'Check Payment Details'<br>"
                    + "4. Upload a clear image of your payment proof<br><br>"
                    + "Important Guidelines for Payment Proof:<br>"
                    + "- Ensure the transfer amount matches the booking amount<br>"
                    + "- The image should be clear and readable<br>"
                    + "- Make sure all transaction details are visible<br>"
                    + "- Accepted formats: JPG or PNG (max 1MB)<br><br>"
                    + "Bank Account Details for Transfer:<br>"
                    + "Bank Name: " + payment.getBooking().getRoom().getProperty().getUser().getBankName() + "<br>"
                    + "Account Number: " + payment.getBooking().getRoom().getProperty().getUser().getBankAccountNumber() + "<br>"
                    + "Account Holder: " + payment.getBooking().getRoom().getProperty().getUser().getBankHolderName() + "<br><br>"
                    + "If you need any assistance or have questions, please don't hesitate to contact our support team.<br><br>"
                    + "Best regards,<br>"
                    + "Nginep Team";

            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("Successfully sent email to {}", toAddress);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send payment confirmation email", e);
        }
    }

}