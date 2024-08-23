package com.example.nginep.payments.service.impl;

import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import com.example.nginep.payments.repository.PaymentRepository;
import com.example.nginep.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment createPaymentForBooking(Long bookingId, BigDecimal amount, PaymentType paymentType) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);
        payment.setStatus(PaymentStatus.PENDING_PAYMENT);

        return paymentRepository.save(payment);
    }
}