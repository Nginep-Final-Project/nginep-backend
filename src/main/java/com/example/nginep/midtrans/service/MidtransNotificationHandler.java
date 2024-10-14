package com.example.nginep.midtrans.service;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.bookings.tasks.CancelUnconfirmedBookingTask;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidtransNotificationHandler {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final MidtransService midtransService;
    private final TaskScheduler taskScheduler;
    private final CancelUnconfirmedBookingTask cancelUnconfirmedBookingTask;

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

            Payment updatedPayment = null;
            Booking updatedBooking = null;

            try {
                updatedPayment = paymentService.updatePaymentStatusMidtrans(orderId, transactionStatus, fraudStatus);
            } catch (Exception e) {
                log.error("Error updating payment status: {}", e.getMessage());
            }

            try {
                updatedBooking = bookingService.updateBookingStatusMidtrans(orderId, transactionStatus, fraudStatus);
            } catch (Exception e) {
                log.error("Error updating booking status: {}", e.getMessage());
            }

            if (updatedPayment != null && updatedBooking != null &&
                    updatedPayment.getStatus() == PaymentStatus.CONFIRMED &&
                    updatedBooking.getStatus() == BookingStatus.AWAITING_CONFIRMATION) {
                scheduleUnconfirmedBookingCancellation(updatedBooking.getId());
            }

        } catch (Exception e) {
            log.error("Failed to process Midtrans notification: {}", e.getMessage());
            throw new ApplicationException("Failed to process Midtrans notification: " + e.getMessage());
        }
    }

    private void scheduleUnconfirmedBookingCancellation(Long bookingId) {
        cancelUnconfirmedBookingTask.setBookingId(bookingId);
        taskScheduler.schedule(cancelUnconfirmedBookingTask, Instant.now().plus(48, ChronoUnit.HOURS));
    }
}