package com.example.nginep.property.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesRequestDto;
import com.example.nginep.peakSeasonRates.service.PeakSeasonRatesService;
import com.example.nginep.property.dto.PropertyRequestDto;
import com.example.nginep.property.dto.PropertyResponseDto;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.repository.PropertyRepository;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.propertyFacility.dto.PropertyFacilityRequestDto;
import com.example.nginep.propertyFacility.service.PropertyFacilityService;
import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.propertyImages.service.PropertyImageService;
import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyFacilityService propertyFacilityService;
    private final PropertyImageService propertyImageService;
    private final RoomService roomService;
    private final UsersService usersService;
    private final PeakSeasonRatesService peakSeasonRatesService;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UsersService usersService,
                               @Lazy PropertyFacilityService propertyFacilityService, @Lazy PropertyImageService propertyImageService,
                               @Lazy RoomService roomService, @Lazy PeakSeasonRatesService peakSeasonRatesService) {
        this.propertyRepository = propertyRepository;
        this.propertyFacilityService = propertyFacilityService;
        this.propertyImageService = propertyImageService;
        this.roomService = roomService;
        this.usersService = usersService;
        this.peakSeasonRatesService = peakSeasonRatesService;
    }


    @Override
    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto) {
        Users user = usersService.getDetailUserId(propertyRequestDto.getTenantId());
        Property newProperty = propertyRepository.save(propertyRequestDto.toEntity(user));
        for (String propertyFacility : propertyRequestDto.getPropertyFacilities()) {
            PropertyFacilityRequestDto newPropertyFacility = new PropertyFacilityRequestDto();
            newPropertyFacility.setValue(propertyFacility);
            newPropertyFacility.setPropertyId(newProperty.getId());
            propertyFacilityService.createPropertyFacility(newPropertyFacility);
        }
        for (PropertyImageRequestDto propertyImageRequestDto : propertyRequestDto.getPropertyImage()) {
            PropertyImageRequestDto newPropertyImage = new PropertyImageRequestDto();
            newPropertyImage.setPath(propertyImageRequestDto.getPath());
            newPropertyImage.setPublicKey(propertyImageRequestDto.getPublicKey());
            newPropertyImage.setIsThumbnail(propertyImageRequestDto.getIsThumbnail());
            newPropertyImage.setPropertyId(newProperty.getId());
            propertyImageService.createPropertyImage(newPropertyImage);
        }
        for (RoomRequestDto roomRequestDto : propertyRequestDto.getRooms()) {
            RoomRequestDto newPropertyRoom = new RoomRequestDto();
            newPropertyRoom.setName(roomRequestDto.getName());
            newPropertyRoom.setDescription(roomRequestDto.getDescription());
            newPropertyRoom.setBasePrice(roomRequestDto.getBasePrice());
            newPropertyRoom.setMaxGuests(roomRequestDto.getMaxGuests());
            newPropertyRoom.setTotalRoom(roomRequestDto.getTotalRoom());
            newPropertyRoom.setNotAvailableDates(roomRequestDto.getNotAvailableDates());
            newPropertyRoom.setPropertyId(newProperty.getId());
            roomService.createRoom(newPropertyRoom);
        }
        for (PeakSeasonRatesRequestDto peakSeasonRatesRequestDto: propertyRequestDto.getPeakSeasonRates()) {
            PeakSeasonRatesRequestDto newPeakSeasonRates = new PeakSeasonRatesRequestDto();
            newPeakSeasonRates.setPeakSeasonDates(peakSeasonRatesRequestDto.getPeakSeasonDates());
            newPeakSeasonRates.setRateType(peakSeasonRatesRequestDto.getRateType());
            newPeakSeasonRates.setRateValue(peakSeasonRatesRequestDto.getRateValue());
            newPeakSeasonRates.setPropertyId(newProperty.getId());
            peakSeasonRatesService.createPeakSeasonRates(newPeakSeasonRates);
        }

        return mapToPropertyResponseDto(newProperty);
    }

    @Override
    public PropertyResponseDto updateProperty(PropertyRequestDto propertyRequestDto) {
        Property property = propertyRepository.findById(propertyRequestDto.getId()).orElseThrow(()->new NotFoundException("Property with id: " + propertyRequestDto.getId() + " not found"));
        property.setPropertyName(propertyRequestDto.getPropertyName());
        property.setPropertyCategory(propertyRequestDto.getPropertyCategory());
        property.setPropertyDescription(propertyRequestDto.getPropertyDescription());
        property.setGuestPlaceType(propertyRequestDto.getGuestPlaceType());
        property.setPropertyAddress(propertyRequestDto.getPropertyAddress());
        property.setPropertyCity(propertyRequestDto.getPropertyCity());
        property.setPropertyProvince(propertyRequestDto.getPropertyProvince());
        property.setPropertyPostalCode(propertyRequestDto.getPropertyPostalCode());
        property.setPropertyLatitude(propertyRequestDto.getPropertyLatitude());
        property.setPropertyLongitude(propertyRequestDto.getPropertyLongitude());
        Property editedProperty = propertyRepository.save(property);
        return mapToPropertyResponseDto(editedProperty);
    }

    @Override
    public List<PropertyResponseDto> GetPropertyByTenantId(Long tenantId) {
        return propertyRepository.findAllByUserId(tenantId).stream().map(this::mapToPropertyResponseDto).toList();
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(() -> new NotFoundException("Property with id: " + propertyId + " not found"));
    }

    @Override
    public String deleteProperty(Long propertyId) {
        propertyRepository.findById(propertyId).orElseThrow(()->new NotFoundException("Property with id: " + propertyId + " not found"));
        propertyRepository.deleteById(propertyId);
        return "Delete property with id: " + propertyId + " success";
    }

    public PropertyResponseDto mapToPropertyResponseDto(Property property) {
        PropertyResponseDto response = new PropertyResponseDto();
        response.setId(property.getId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyCategory(property.getPropertyCategory());
        response.setPropertyDescription(property.getPropertyDescription());
        response.setPropertyFacilities(propertyFacilityService.getFacilityByPropertyId(property.getId()));
        response.setPropertyImage(propertyImageService.getPropertyImageByPropertyId(property.getId()));
        response.setGuestPlaceType(property.getGuestPlaceType());
        response.setPropertyAddress(property.getPropertyAddress());
        response.setPropertyCity(property.getPropertyCity());
        response.setPropertyProvince(property.getPropertyProvince());
        response.setPropertyPostalCode(property.getPropertyPostalCode());
        response.setPropertyLatitude(property.getPropertyLatitude());
        response.setPropertyLongitude(property.getPropertyLongitude());
        response.setRooms(roomService.getRoomByPropertyId(property.getId()));
        response.setPeakSeasonRate(peakSeasonRatesService.getPeakSeasonRatesByPropertyId(property.getId()));
        response.setTenantId(property.getUser().getId());
        return response;
    }
}
