package com.example.nginep.rooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer maxGuests;
    private Long propertyId;
}
