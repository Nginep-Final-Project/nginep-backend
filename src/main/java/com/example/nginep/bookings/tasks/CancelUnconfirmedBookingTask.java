package com.example.nginep.bookings.tasks;

import com.example.nginep.bookings.service.BookingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class CancelUnconfirmedBookingTask implements Runnable {
    private final BookingService bookingService;
    private Long bookingId;

    @Override
    public void run() {
        bookingService.cancelBookingIfNotConfirmed(bookingId);
    }
}