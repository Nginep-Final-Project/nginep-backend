package com.example.nginep.orchestrator;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.payments.entity.Payment;
import lombok.Data;

import java.util.Map;

@Data
public class BookingPaymentResult {
    private Booking booking;
    private Payment payment;
    private Map<String, Object> midtransResponse;

    public BookingPaymentResult(Booking booking, Payment payment, Map<String, Object> midtransResponse) {
        this.booking = booking;
        this.payment = payment;
        this.midtransResponse = midtransResponse;
    }
}