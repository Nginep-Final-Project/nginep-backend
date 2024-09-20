package com.example.nginep.bookings.dto;

import com.example.nginep.bookings.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserBookingsDto {
    private Long bookingId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numGuests;
    private BookingStatus status;
    private String hostName;
    private String roomName;
    private String propertyName;
    private String propertyAddress;
    private String propertyCity;
    private String propertyProvince;
    private String propertyCoverImage;
}