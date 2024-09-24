package com.example.nginep.propertyFacility.service;

import com.example.nginep.propertyFacility.dto.PropertyFacilityRequestDto;
import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyFacility.entity.PropertyFacility;

import java.util.List;

public interface PropertyFacilityService {
    PropertyFacilityResponseDto createPropertyFacility(PropertyFacilityRequestDto propertyFacilityRequestDto);

    List<PropertyFacilityResponseDto> getFacilityByPropertyId(Long propertyId);

    String deletePropertyFacility(Long propertyFacilityId);
}
