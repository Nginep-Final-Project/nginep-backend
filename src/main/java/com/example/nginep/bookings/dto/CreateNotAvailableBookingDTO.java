package com.example.nginep.bookings.dto;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateNotAvailableBookingDTO {
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal finalPrice;
    private Integer numGuests;

    public Booking toEntity(){
        Booking newBooking = new Booking();
        newBooking.setUserId(userId);
        newBooking.setRoomId(roomId);
        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);
        newBooking.setFinalPrice(finalPrice);
        newBooking.setNumGuests(numGuests);
        newBooking.setStatus(BookingStatus.NOT_AVAILABLE);
        return newBooking;
    }
}
