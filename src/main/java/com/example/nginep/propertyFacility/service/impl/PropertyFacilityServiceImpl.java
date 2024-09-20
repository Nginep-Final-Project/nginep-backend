package com.example.nginep.propertyFacility.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.propertyFacility.dto.PropertyFacilityRequestDto;
import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyFacility.entity.PropertyFacility;
import com.example.nginep.propertyFacility.repository.PropertyFacilityRepository;
import com.example.nginep.propertyFacility.service.PropertyFacilityService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class PropertyFacilityServiceImpl implements PropertyFacilityService {
    private final PropertyFacilityRepository propertyFacilityRepository;
    private final PropertyService propertyService;

    public PropertyFacilityServiceImpl(PropertyFacilityRepository propertyFacilityRepository, PropertyService propertyService) {
        this.propertyFacilityRepository = propertyFacilityRepository;
        this.propertyService = propertyService;
    }


    @Override
    public PropertyFacilityResponseDto createPropertyFacility(PropertyFacilityRequestDto propertyFacilityRequestDto) {
        Property property = propertyService.getPropertyById(propertyFacilityRequestDto.getPropertyId());
        PropertyFacility newPropertyFacility = propertyFacilityRepository.save(propertyFacilityRequestDto.toEntity(property));
        log.info(newPropertyFacility.toString());
        return mapToPropertyFacilityResponseDto(newPropertyFacility);
    }

    @Override
    public List<PropertyFacilityResponseDto> getFacilityByPropertyId(Long propertyId) {
        return propertyFacilityRepository.findAllByPropertyId(propertyId).stream().map(this::mapToPropertyFacilityResponseDto).toList();
    }

    @Override
    public String deletePropertyFacility(Long propertyFacilityId) {
        propertyFacilityRepository.findById(propertyFacilityId).orElseThrow(() -> new NotFoundException("Property facility with id: " + propertyFacilityId + " not found"));
        propertyFacilityRepository.deleteById(propertyFacilityId);
        return "Property facility with id: " + propertyFacilityId + " has deleted successfully";
    }

    public PropertyFacilityResponseDto mapToPropertyFacilityResponseDto(PropertyFacility propertyFacility) {
        PropertyFacilityResponseDto response = new PropertyFacilityResponseDto();
        response.setId(propertyFacility.getId());
        response.setValue(propertyFacility.getValue());
        response.setPropertyId(propertyFacility.getProperty().getId());
        return response;
    }
}
