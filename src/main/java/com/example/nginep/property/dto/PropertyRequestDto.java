package com.example.nginep.property.dto;

import com.example.nginep.property.entity.DateRange;
import com.example.nginep.property.entity.PeakSeasonRate;
import com.example.nginep.property.entity.Property;
import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.users.entity.Users;
import lombok.Data;

import java.util.List;

@Data
public class PropertyRequestDto {
    private Long id;
    private String propertyName;
    private String propertyCategory;
    private String propertyDescription;
    private List<String> propertyFacilities;
    private List<PropertyImageRequestDto> propertyImage;
    private String guestPlaceType;
    private String propertyAddress;
    private String propertyCity;
    private String propertyProvince;
    private String propertyPostalCode;
    private Double propertyLatitude;
    private Double propertyLongitude;
    private DateRange notAvailabilityDates;
    private DateRange peakSeasonDates;
    private List<RoomRequestDto> rooms;
    private PeakSeasonRate peakSeasonRate;
    private Long tenantId;

    public Property toEntity(Users user){
        Property newProperty = new Property();
        newProperty.setPropertyName(propertyName);
        newProperty.setPropertyCategory(propertyCategory);
        newProperty.setPropertyDescription(propertyDescription);
        newProperty.setGuestPlaceType(guestPlaceType);
        newProperty.setPropertyAddress(propertyAddress);
        newProperty.setPropertyCity(propertyCity);
        newProperty.setPropertyProvince(propertyProvince);
        newProperty.setPropertyPostalCode(propertyPostalCode);
        newProperty.setPropertyLatitude(propertyLatitude);
        newProperty.setPropertyLongitude(propertyLongitude);
        newProperty.setNotAvailabilityDates(notAvailabilityDates);
        newProperty.setPeakSeasonDates(peakSeasonDates);
        newProperty.setPeakSeasonRate(peakSeasonRate);
        newProperty.setUser(user);
        return newProperty;
    }
}