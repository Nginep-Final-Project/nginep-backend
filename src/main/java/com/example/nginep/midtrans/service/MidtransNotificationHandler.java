package com.example.nginep.midtrans.service;

import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MidtransNotificationHandler {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final MidtransService midtransService;

    @Transactional
    public void handleNotification(String notificationPayload) {
        try {
            JSONObject notification = new JSONObject(notificationPayload);

            String orderId = notification.getString("order_id");
            String transactionStatus = notification.getString("transaction_status");
            String fraudStatus = notification.optString("fraud_status");

            JSONObject transactionStatusValidate = midtransService.getTransactionStatus(orderId);
            if (!transactionStatus.equals(transactionStatusValidate.getString("transaction_status"))) {
                throw new ApplicationException("Transaction status mismatch");
            }

            paymentService.updatePaymentStatusMidtrans(orderId, transactionStatus, fraudStatus);

            bookingService.updateBookingStatusMidtrans(orderId, transactionStatus, fraudStatus);

        } catch (Exception e) {
            throw new ApplicationException("Failed to process Midtrans notification: " + e.getMessage());
        }
    }
}