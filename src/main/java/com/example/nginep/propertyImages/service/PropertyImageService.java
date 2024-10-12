package com.example.nginep.propertyImages.service;

import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;

import java.util.List;


public interface PropertyImageService {
    PropertyImageResponseDto createPropertyImage(PropertyImageRequestDto propertyImageRequestDto);

    String setThumbnailImage(PropertyImageRequestDto propertyImageRequestDto);

    List<PropertyImageResponseDto> getPropertyImageByPropertyId(Long propertyId);

    String deletePropertyImage(Long propertyImageId);
}
