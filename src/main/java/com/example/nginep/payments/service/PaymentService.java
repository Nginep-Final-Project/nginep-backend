package com.example.nginep.payments.service;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.payments.dto.UploadProofOfPaymentDto;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createPayment(Booking booking, BigDecimal amount, PaymentType paymentType, String bank);

    Payment uploadProofOfPayment(UploadProofOfPaymentDto uploadProofOfPaymentDTO);

    Payment confirmManualPayment(Long paymentId);

    Payment rejectPayment(Long paymentId);

    PaymentStatus getPaymentStatusForBooking(Long bookingId);

    Map<String, Object> getMidtransDetails(String orderId);

    Payment updatePaymentStatusMidtrans(Payment payment, String transactionStatus, String fraudStatus);

    void cancelUnconfirmedManualPayment(Long paymentId);

    Payment findPaymentByOrderId(String orderId);

    void sendPaymentConfirmationEmail(Payment payment);
}