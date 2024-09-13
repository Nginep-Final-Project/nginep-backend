package com.example.nginep.property.dto;

import com.example.nginep.property.entity.DateRange;
import com.example.nginep.property.entity.PeakSeasonRate;
import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.room.dto.RoomResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class PropertyResponseDto {
    private Long id;
    private String propertyName;
    private String propertyCategory;
    private String propertyDescription;
    private List<PropertyFacilityResponseDto> propertyFacilities;
    private List<PropertyImageResponseDto> propertyImage;
    private String guestPlaceType;
    private String propertyAddress;
    private String propertyCity;
    private String propertyProvince;
    private String propertyPostalCode;
    private Double propertyLatitude;
    private Double propertyLongitude;
    private DateRange notAvailabilityDates;
    private DateRange peakSeasonDates;
    private List<RoomResponseDto> rooms;
    private PeakSeasonRate peakSeasonRate;
    private Long tenantId;
}
