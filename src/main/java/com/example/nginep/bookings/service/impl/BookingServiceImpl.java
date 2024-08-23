package com.example.nginep.bookings.service.impl;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.repository.BookingRepository;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.payments.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setUserId(bookingDTO.getUserId());
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setNumGuests(bookingDTO.getNumGuests());
        booking.setUserMessage(bookingDTO.getUserMessage());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        // Calculate yg bener final price-nya dari table room nanti
        booking.setFinalPrice(BigDecimal.ZERO);

        Booking savedBooking = bookingRepository.save(booking);

        paymentService.createPaymentForBooking(
                savedBooking.getId(),
                savedBooking.getFinalPrice(),
                bookingDTO.getPaymentMethod()
        );

        return savedBooking;
    }
}