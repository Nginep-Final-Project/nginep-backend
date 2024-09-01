package com.example.nginep.payments.service.impl;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.cloudinary.dto.CloudinaryUploadResponseDto;
import com.example.nginep.cloudinary.service.CloudinaryService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.payments.dto.UploadProofOfPaymentDto;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import com.example.nginep.payments.repository.PaymentRepository;
import com.example.nginep.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CloudinaryService cloudinaryService;

    private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("jpg", "png");
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private static final int MAX_PAYMENT_ATTEMPTS = 3;

    @Override
    public Payment createPaymentForBooking(Long bookingId, BigDecimal amount, PaymentType paymentType) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);
        payment.setStatus(PaymentStatus.PENDING_PAYMENT);
        payment.setExpiryTime(Instant.now().plus(1, ChronoUnit.HOURS));
        payment.setAttempts(0);

        return paymentRepository.save(payment);
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

    @Override
    @Transactional
    public Payment confirmManualPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.AWAITING_CONFIRMATION) {
            throw new ApplicationException("Payment is not in a state to be confirmed");
        }

        payment.setStatus(PaymentStatus.CONFIRMED);
        return paymentRepository.save(payment);
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
}