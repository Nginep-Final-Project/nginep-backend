package com.example.nginep.rooms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchAvailableRoomRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalGuest;
    private Long propertyId;
}
