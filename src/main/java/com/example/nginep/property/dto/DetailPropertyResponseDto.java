package com.example.nginep.property.dto;

import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.rooms.dto.RoomResponseDto;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.users.dto.UsersResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class DetailPropertyResponseDto {
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
    private List<RoomResponseDto> rooms;
    private List<PeakSeasonRatesResponseDto> peakSeasonRate;
    private PropertyReviewSummaryDto reviewSummary;
    private List<ReviewDto> reviewList;
    private UsersResponseDto tenant;

}
