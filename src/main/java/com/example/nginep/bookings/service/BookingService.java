package com.example.nginep.bookings.service;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.entity.Booking;

public interface BookingService {
    Booking createBooking(CreateBookingDTO bookingDTO);
}