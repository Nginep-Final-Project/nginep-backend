package com.example.nginep.bookings.controller;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.orchestrator.BookingPaymentOrchestrator;
import com.example.nginep.orchestrator.BookingPaymentResult;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingPaymentOrchestrator bookingPaymentOrchestrator;

    @PostMapping("/create")
    public ResponseEntity<Response<BookingPaymentResult>> createBooking(@RequestBody CreateBookingDTO bookingDTO) {
        BookingPaymentResult newBooking = bookingPaymentOrchestrator.createBookingWithPayment(bookingDTO);
        return Response.successResponse("Booking created successfully", newBooking);
    }

//    @GetMapping("/tenant/{tenantId}")
//    public ResponseEntity<Response<List<Booking>>> getTenantBookings(
//            @PathVariable Long tenantId,
//            @RequestParam(required = false) BookingStatus status) {
//        List<Booking> bookings = bookingService.getTenantBookings(tenantId, status);
//        return Response.successResponse("Tenant bookings retrieved successfully", bookings);
//    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<Response<Booking>> confirmBooking(@PathVariable Long bookingId) {
        Booking confirmedBooking = bookingService.confirmBooking(bookingId);
        return Response.successResponse("Booking confirmed successfully", confirmedBooking);
    }

    @PostMapping("/{bookingId}/cancel/tenant")
    public ResponseEntity<Response<Booking>> cancelBookingByTenant(@PathVariable Long bookingId) {
        Booking cancelledBooking = bookingService.cancelBookingByTenant(bookingId);
        return Response.successResponse("Booking cancelled successfully by tenant", cancelledBooking);
    }

    @PostMapping("/{bookingId}/cancel/user")
    public ResponseEntity<Response<Booking>> cancelBookingByUser(@PathVariable Long bookingId) {
        Booking cancelledBooking = bookingService.cancelBookingByUser(bookingId);
        return Response.successResponse("Booking cancelled successfully by user", cancelledBooking);
    }
}