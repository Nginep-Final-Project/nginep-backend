package com.example.nginep.bookings.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UnreviewedBookingDto {
    private Long id;
    private String propertyName;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String propertyCoverImage;
}