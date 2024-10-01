package com.example.nginep.analytics.dto;

import lombok.Data;

import java.util.List;

@Data
public class PropertyAvailabilityDto {
    private Long propertyId;
    private String propertyName;
    private List<RoomAvailabilityDto> rooms;

    @Data
    public static class RoomAvailabilityDto {
        private Long roomId;
        private String roomName;
        private List<UnavailableDateRangeDto> unavailableDates;
    }

    @Data
    public static class UnavailableDateRangeDto {
        private String startDate;
        private String endDate;
    }
}