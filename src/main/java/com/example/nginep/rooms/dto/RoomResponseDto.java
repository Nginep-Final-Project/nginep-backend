package com.example.nginep.rooms.dto;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private Integer totalRoom;
    private CreateBookingDTO Booking;
}
