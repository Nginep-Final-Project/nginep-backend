package com.example.nginep.bookings.controller;

import com.example.nginep.bookings.dto.*;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.orchestrator.BookingPaymentOrchestrator;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingPaymentOrchestrator bookingPaymentOrchestrator;

    @GetMapping("/user")
    public ResponseEntity<Response<List<UserBookingsDto>>> getUserBookings() {
        List<UserBookingsDto> bookings = bookingService.getUserBookings();
        return Response.successResponse("User bookings retrieved successfully", bookings);
    }

    @GetMapping("/tenant")
    public ResponseEntity<Response<List<TenantBookingsDto>>> getTenantBookings() {
        List<TenantBookingsDto> bookings = bookingService.getTenantBookings();
        return Response.successResponse("Tenant bookings retrieved successfully", bookings);
    }

    @GetMapping("/{bookingId}/payment-details")
    public ResponseEntity<Response<BookingPaymentDetailsDto>> getBookingPaymentDetails(@PathVariable Long bookingId) {
        BookingPaymentDetailsDto details = bookingService.getBookingPaymentDetails(bookingId);
        return Response.successResponse("Booking payment details retrieved successfully", details);
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Map<String, Long>>> createBooking(@RequestBody CreateBookingDto bookingDTO) {
        Long bookingId = bookingPaymentOrchestrator.createBookingWithPayment(bookingDTO);
        Map<String, Long> responseData = Map.of("bookingId", bookingId);
        return Response.successResponse("Booking created successfully", responseData);
    }

    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<Response<Booking>> confirmBooking(@PathVariable Long bookingId) {
        Booking confirmedBooking = bookingService.confirmBooking(bookingId);
        return Response.successResponse("Booking confirmed successfully", confirmedBooking);
    }

    @PatchMapping("/{bookingId}/cancel/tenant")
    public ResponseEntity<Response<Object>> cancelBookingByTenant(@PathVariable Long bookingId) {
        bookingService.cancelBookingByTenant(bookingId);
        return Response.successResponse("Booking cancelled successfully by tenant");
    }

    @PatchMapping("/{bookingId}/cancel/user")
    public ResponseEntity<Response<Object>> cancelBookingByUser(@PathVariable Long bookingId) {
        bookingService.cancelBookingByUser(bookingId);
        return Response.successResponse("Booking cancelled successfully by user");
    }

    @GetMapping("/check-existing-pending-booking")
    public ResponseEntity<Response<Long>> checkExistingPendingBooking(
            @RequestParam Long roomId) {
        Long existingBookingId = bookingService.checkExistingPendingBooking(roomId);
        if (existingBookingId != null) {
            return Response.successResponse("Existing pending booking found", existingBookingId);
        } else {
            return Response.successResponse("No existing pending booking found", null);
        }
    }

    @PostMapping("/not-available-booking")
    public ResponseEntity<Response<Booking>> createNotAvailableBooking(@RequestBody CreateNotAvailableBookingDTO createNotAvailableBookingDTO) {
        return Response.successResponse("Create not available booking success", bookingService.createNotAvailableBooking(createNotAvailableBookingDTO));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response<List<Booking>>> getBookingByRoomId(@PathVariable Long roomId) {
        return Response.successResponse("Get list booking by room id success", bookingService.getBookingByRoomId(roomId));
    }

    @PutMapping("/not-available-booking")
    public ResponseEntity<Response<Booking>> editNotAvailableBooking(@RequestBody CreateNotAvailableBookingDTO createNotAvailableBookingDTO) {
        return Response.successResponse("Edit not available booking success", bookingService.editNotAvailableBooking(createNotAvailableBookingDTO));
    }

    @DeleteMapping("/not-available-booking/{bookingId}")
    public ResponseEntity<Response<String>> editNotAvailableBooking(@PathVariable Long bookingId) {
        return Response.successResponse("Delete not available booking success", bookingService.deleteNotAvailableBooking(bookingId));
    }

    @GetMapping("/user/unreviewed")
    public ResponseEntity<Response<List<UnreviewedBookingDto>>> getUnreviewedBookings() {
        List<UnreviewedBookingDto> unreviewedBookings = bookingService.getUnreviewedBookingsForUser();
        return Response.successResponse("Unreviewed bookings retrieved successfully", unreviewedBookings);
    }

}