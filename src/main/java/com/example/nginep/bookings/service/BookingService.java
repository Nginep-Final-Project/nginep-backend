package com.example.nginep.bookings.service;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.dto.CreateNotAvailableBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking createBooking(CreateBookingDTO bookingDTO);

    Booking createNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO);

    void updateBookingStatus(Long bookingId, BookingStatus status);

//    List<Booking> getTenantBookings(Long tenantId, BookingStatus status);

    Booking confirmBooking(Long bookingId);

    Booking cancelBookingByTenant(Long bookingId);

    Booking cancelBookingByUser(Long bookingId);

    Booking updateBookingStatusMidtrans(String orderId, String transactionStatus, String fraudStatus);
}