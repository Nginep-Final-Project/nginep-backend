package com.example.nginep.payments.service;

import com.example.nginep.payments.dto.UploadProofOfPaymentDto;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;

import java.math.BigDecimal;

public interface PaymentService {
    Payment createPaymentForBooking(Long bookingId, BigDecimal amount, PaymentType paymentType);

    Payment uploadProofOfPayment(UploadProofOfPaymentDto uploadProofOfPaymentDTO);

    Payment confirmManualPayment(Long paymentId);

    Payment rejectPayment(Long paymentId);

    PaymentStatus getPaymentStatusForBooking(Long bookingId);
}