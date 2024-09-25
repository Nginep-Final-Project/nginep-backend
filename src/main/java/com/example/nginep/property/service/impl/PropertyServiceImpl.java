package com.example.nginep.property.service.impl;

import com.example.nginep.category.dto.CategoryResponseDto;
import com.example.nginep.category.repository.CategoryRepository;
import com.example.nginep.category.service.CategoryService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesRequestDto;
import com.example.nginep.peakSeasonRates.service.PeakSeasonRatesService;
import com.example.nginep.property.dto.*;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.repository.PropertyRepository;
import com.example.nginep.property.repository.PropertySpecification;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.propertyFacility.dto.PropertyFacilityRequestDto;
import com.example.nginep.propertyFacility.service.PropertyFacilityService;
import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.propertyImages.service.PropertyImageService;
import com.example.nginep.reviews.service.ReviewService;
import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.rooms.dto.SearchAvailableRoomRequestDto;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final ReviewService reviewService;
    private final CategoryService categoryService;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UsersService usersService,
                               @Lazy PropertyFacilityService propertyFacilityService, @Lazy PropertyImageService propertyImageService,
                               @Lazy RoomService roomService, @Lazy PeakSeasonRatesService peakSeasonRatesService,
                               ReviewService reviewService, CategoryService categoryService) {
        this.propertyRepository = propertyRepository;
        this.propertyFacilityService = propertyFacilityService;
        this.propertyImageService = propertyImageService;
        this.roomService = roomService;
        this.usersService = usersService;
        this.peakSeasonRatesService = peakSeasonRatesService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
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
    public List<PropertyResponseDto> getPropertyByTenantId(Long tenantId) {
        return propertyRepository.findAllByUserId(tenantId).stream().map(this::mapToPropertyResponseDto).toList();
    }

    @Override
    public Page<SearchResponseDto> getAllProperty(Pageable pageable, String propertyName, String propertyCategory, String propertyCity, LocalDate checkInDate, LocalDate checkOutDate, Integer totalGuests) {
        Specification<Property> specification = Specification.where(PropertySpecification.byPropertyName(propertyName))
                .and(PropertySpecification.byPropertyCategory(propertyCategory))
                .and(PropertySpecification.byPropertyCity(propertyCity))
                .and(PropertySpecification.byAvailableWithinDates(checkInDate, checkOutDate))
                .and(PropertySpecification.byTotalGuests(totalGuests));

        return propertyRepository.findAll(specification, pageable).map(this::mapToSearchResponseDto);
    }

    @Override
    public List<PropertyCitiesResponseDto> getAllCities() {
        List<String> cities = propertyRepository.findDistinctCities();
        List<PropertyCitiesResponseDto> convertedCities = new ArrayList<>();
        for (String city : cities) {
            PropertyCitiesResponseDto convert = new PropertyCitiesResponseDto();
            convert.setLabel(city);
            convert.setValue(city.trim().toLowerCase().replace(" ", "-"));
            convertedCities.add(convert);
        }
        return convertedCities;
    }

    @Override
    public HomeResponseDto getHomeData() {
        List<CategoryResponseDto> categories = categoryService.getAllCategory();
        Pageable pageable = PageRequest.of(0, 12);
        HomeResponseDto homeResponse = new HomeResponseDto();
        homeResponse.setCategories(categories);
        homeResponse.setCities(getAllCities());
        homeResponse.setProperties(getAllProperty(pageable,null,null,null,null,null,null));
        return homeResponse;
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(() -> new NotFoundException("Property with id: " + propertyId + " not found"));
    }

    @Override
    public DetailPropertyResponseDto getDetailProperty(Long propertyId) {
        Property property = getPropertyById(propertyId);

        SearchAvailableRoomRequestDto requestSearchRoom = new SearchAvailableRoomRequestDto();
        requestSearchRoom.setStartDate(LocalDate.now());
        requestSearchRoom.setEndDate(LocalDate.now().plusDays(1));
        requestSearchRoom.setTotalGuest(1);
        requestSearchRoom.setPropertyId(propertyId);

        DetailPropertyResponseDto detail = new DetailPropertyResponseDto();
        detail.setId(property.getId());
        detail.setPropertyName(property.getPropertyName());
        detail.setPropertyCategory(property.getPropertyCategory());
        detail.setPropertyDescription(property.getPropertyDescription());
        detail.setPropertyFacilities(propertyFacilityService.getFacilityByPropertyId(property.getId()));
        detail.setPropertyImage(propertyImageService.getPropertyImageByPropertyId(property.getId()));
        detail.setGuestPlaceType(property.getGuestPlaceType());
        detail.setPropertyAddress(property.getPropertyAddress());
        detail.setPropertyCity(property.getPropertyCity());
        detail.setPropertyProvince(property.getPropertyProvince());
        detail.setPropertyPostalCode(property.getPropertyPostalCode());
        detail.setPropertyLatitude(property.getPropertyLatitude());
        detail.setPropertyLongitude(property.getPropertyLongitude());
        detail.setRooms(roomService.searchRoomAvailable(requestSearchRoom));
        detail.setPeakSeasonRate(peakSeasonRatesService.getPeakSeasonRatesByPropertyId(property.getId()));
        detail.setTenant(usersService.getDetailUser(property.getUser().getEmail()));
        detail.setReviewSummary(reviewService.getPropertyReviewSummary(property.getId()));
        detail.setReviewList(reviewService.getTopReviewsByPropertyId(property.getId(), 7));
        return detail;
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

    public SearchResponseDto mapToSearchResponseDto(Property property) {
        SearchResponseDto response = new SearchResponseDto();
        response.setId(property.getId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyCategory(property.getPropertyCategory());
        response.setPropertyImage(propertyImageService.getPropertyImageByPropertyId(property.getId()));
        response.setPropertyAddress(property.getPropertyAddress());
        response.setPropertyCity(property.getPropertyCity());
        response.setPropertyProvince(property.getPropertyProvince());
        response.setRooms(roomService.getRoomByPropertyId(property.getId()));
        response.setRating(reviewService.getPropertyReviewSummary(property.getId()).getAverageRating());
        return response;
    }
}
