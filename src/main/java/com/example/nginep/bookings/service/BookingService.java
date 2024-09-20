package com.example.nginep.bookings.service;

import com.example.nginep.bookings.dto.BookingPaymentDetailsDto;
import com.example.nginep.bookings.dto.TenantBookingsDto;
import com.example.nginep.bookings.dto.UserBookingsDto;
import com.example.nginep.bookings.dto.CreateBookingDto;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking createBooking(CreateBookingDto bookingDTO);

    void updateBookingStatus(Long bookingId, BookingStatus status);

    Booking confirmBooking(Long bookingId);

    void cancelBookingByTenant(Long bookingId);

    void cancelBookingByUser(Long bookingId);

    Booking updateBookingStatusMidtrans(String orderId, String transactionStatus, String fraudStatus);

    List<UserBookingsDto> getUserBookings(Long userId);

    List<TenantBookingsDto> getTenantBookings(Long tenantId);

    BookingPaymentDetailsDto getBookingPaymentDetails(Long bookingId);

    Long checkExistingPendingBooking(Long userId, Long roomId);

    void cancelBookingIfPending(Long bookingId);

    Booking findBookingById(Long bookingId);

}