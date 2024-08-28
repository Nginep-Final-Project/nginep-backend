package com.example.nginep.bookings.controller;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<Response<Booking>> createBooking(@RequestBody CreateBookingDTO bookingDTO) {
        Booking newBooking = bookingService.createBooking(bookingDTO);
        return Response.successResponse("Booking created successfully", newBooking);
    }
}