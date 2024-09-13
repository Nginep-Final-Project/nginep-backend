package com.example.nginep.propertyImages.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.propertyImages.entity.PropertyImage;
import com.example.nginep.propertyImages.repository.PropertyImageRepository;
import com.example.nginep.propertyImages.service.PropertyImageService;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class PropertyImageServiceImpl implements PropertyImageService {
    private final PropertyImageRepository propertyImageRepository;
    private final PropertyService propertyService;

    public PropertyImageServiceImpl(PropertyImageRepository propertyImageRepository, PropertyService propertyService){
        this.propertyImageRepository = propertyImageRepository;
        this.propertyService = propertyService;
    }

    @Override
    public PropertyImageResponseDto createPropertyImage(PropertyImageRequestDto propertyImageRequestDto) {
        Property property = propertyService.getPropertyById(propertyImageRequestDto.getPropertyId());
        PropertyImage newPropertyImage = propertyImageRepository.save(propertyImageRequestDto.toEntity(property));
        return mapToPropertyImageResponseDto(newPropertyImage);
    }

    @Override
    public List<PropertyImageResponseDto> getPropertyImageByPropertyId(Long propertyId) {
        return propertyImageRepository.findAllByPropertyId(propertyId).stream().map(this::mapToPropertyImageResponseDto).toList();
    }

    @Override
    public String deletePropertyImage(Long propertyImageId) {
        propertyImageRepository.findById(propertyImageId).orElseThrow(() -> new NotFoundException("Property image with id: " + propertyImageId + " not found"));
        propertyImageRepository.deleteById(propertyImageId);
        return "Property image with id: " + propertyImageId + " has deleted successfully";
    }

    public PropertyImageResponseDto mapToPropertyImageResponseDto(PropertyImage propertyImage) {
        PropertyImageResponseDto response = new PropertyImageResponseDto();
        response.setId(propertyImage.getId());
        response.setPath(propertyImage.getPath());
        response.setPublicKey(propertyImage.getPublicKey());
        response.setIsThumbnail(propertyImage.getIsThumbnail());
        response.setPropertyId(propertyImage.getProperty().getId());
        return response;
    }
}
