package com.example.nginep.room.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer maxOccupancy;
    private Long propertyId;
}
