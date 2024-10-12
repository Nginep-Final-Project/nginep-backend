package com.example.nginep.property.dto;

import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.rooms.dto.RoomResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponseDto {
    private Long id;
    private String propertyName;
    private String propertyCategory;
    private List<PropertyImageResponseDto> propertyImage;
    private String propertyAddress;
    private String propertyCity;
    private String propertyProvince;
    private List<RoomResponseDto> rooms;
    private Double rating;
}
