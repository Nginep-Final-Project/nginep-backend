package com.example.nginep.property.service;

import com.example.nginep.property.dto.PropertyRequestDto;
import com.example.nginep.property.dto.PropertyResponseDto;
import com.example.nginep.property.entity.Property;

import java.util.List;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateProperty(PropertyRequestDto propertyRequestDto);

    List<PropertyResponseDto> GetPropertyByTenantId(Long tenantId);

    Property getPropertyById(Long propertyId);

    String deleteProperty(Long propertyId);
}
