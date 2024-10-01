package com.example.nginep.rooms.dto;

import com.example.nginep.bookings.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomResponseDto {
    private Long id;
    private String name;
    private String roomPicture;
    private String roomPictureId;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private Integer totalRoom;
    private List<Booking> Booking;
}
