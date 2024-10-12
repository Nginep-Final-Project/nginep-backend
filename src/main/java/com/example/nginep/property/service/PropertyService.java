package com.example.nginep.property.service;

import com.example.nginep.property.dto.*;
import com.example.nginep.property.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateProperty(PropertyRequestDto propertyRequestDto);

    List<PropertyResponseDto> getPropertyByTenantId(Long tenantId);

    Page<SearchResponseDto> getAllProperty(Pageable pageable, String propertyName, String propertyCategory, String propertyCity, LocalDate checkInDate, LocalDate checkOutDate, Integer totalGuests);

    List<PropertyCitiesResponseDto> getAllCities();

    HomeResponseDto getHomeData();

    Property getPropertyById(Long propertyId);

    DetailPropertyResponseDto getDetailProperty(Long propertyId);

    String deleteProperty(Long propertyId);

    Long countPropertiesByTenant(Long tenantId);

    Page<PropertyResponseDto> getPropertyList(Pageable pageable);
}
