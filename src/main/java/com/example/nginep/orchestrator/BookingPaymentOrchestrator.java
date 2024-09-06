package com.example.nginep.orchestrator;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingPaymentOrchestrator {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @Transactional
    public BookingPaymentResult createBookingWithPayment(CreateBookingDTO bookingDTO) {
        Booking booking = bookingService.createBooking(bookingDTO);
        Map<String, Object> paymentResult = paymentService.createPayment(
                booking.getId(),
                booking.getFinalPrice(),
                bookingDTO.getPaymentMethod(),
                bookingDTO.getBank()
        );
        Payment payment = (Payment) paymentResult.get("payment");
        Map<String, Object> midtransResponse = (Map<String, Object>) paymentResult.get("midtransResponse");
        return new BookingPaymentResult(booking, payment, midtransResponse);
    }

    @Transactional
    public void rejectPayment(Long paymentId) {
        Payment payment = paymentService.rejectPayment(paymentId);
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            bookingService.updateBookingStatus(payment.getBookingId(), BookingStatus.CANCELLED);
        }
    }
}