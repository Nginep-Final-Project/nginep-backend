package com.example.nginep.bookings.service;

import com.example.nginep.bookings.dto.*;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking createBooking(CreateBookingDto bookingDTO);

    Booking createNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO);

    void updateBookingStatus(Long bookingId, BookingStatus status);

    List<Booking> getBookingByRoomId(Long roomId);

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

    Booking editNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO);

    String deleteNotAvailableBooking(Long bookingId);

    List<UnreviewedBookingDto> getUnreviewedBookingsForUser(Long userId);
}