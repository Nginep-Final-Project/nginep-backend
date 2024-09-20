package com.example.nginep.bookings.dto;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.users.entity.Users;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateNotAvailableBookingDTO {
    private Long id;
    private Users user;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal finalPrice;
    private Integer numGuests;

    public Booking toEntity(){
        Booking newBooking = new Booking();
        newBooking.setUser(user);
        newBooking.setRoom(room);
        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);
        newBooking.setFinalPrice(finalPrice);
        newBooking.setNumGuests(numGuests);
        newBooking.setStatus(BookingStatus.NOT_AVAILABLE);
        return newBooking;
    }
}
