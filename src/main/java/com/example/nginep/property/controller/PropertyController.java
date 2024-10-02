package com.example.nginep.property.controller;

import com.example.nginep.property.dto.*;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/property")
@Validated
@Log
public class PropertyController {
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<Response<PropertyResponseDto>> createProperty(@RequestBody PropertyRequestDto propertyRequestDto) {
        return Response.successResponse("Create property success", propertyService.createProperty(propertyRequestDto));
    }

    @PutMapping
    public ResponseEntity<Response<PropertyResponseDto>> updateProperty(@RequestBody PropertyRequestDto propertyRequestDto) {
        return Response.successResponse("Update property success", propertyService.updateProperty(propertyRequestDto));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response<DetailPropertyResponseDto>> getDetailProperty(@PathVariable Long propertyId) {
        return Response.successResponse("Get detail property success", propertyService.getDetailProperty(propertyId));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Response<String>> deleteProperty(@PathVariable Long propertyId) {
        return Response.successResponse("Delete property success", propertyService.deleteProperty(propertyId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Response<List
            <PropertyResponseDto>>> getPropertiesByTenantId(@PathVariable Long tenantId) {
        List<PropertyResponseDto> properties = propertyService.getPropertyByTenantId(tenantId);
        return Response.successResponse("Properties retrieved successfully", properties);
    }

    @GetMapping
    public ResponseEntity<Response<Page<SearchResponseDto>>> getAllProperties(
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyCategory,
            @RequestParam(required = false) String propertyCity,
            @RequestParam(required = false) LocalDate checkinDate,
            @RequestParam(required = false) LocalDate checkoutDate,
            @RequestParam(required = false) Integer totalGuests,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) SortDirection sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageableWithSort = createPageAble(sortBy, sortDirection, page, size);

        return Response.successResponse("All events fetched", propertyService.getAllProperty(pageableWithSort,
                propertyName, propertyCategory, propertyCity, checkinDate, checkoutDate, totalGuests));
    }

    @GetMapping("/home")
    public ResponseEntity<Response<HomeResponseDto>> getHomeData() {
        return Response.successResponse("Get home data success", propertyService.getHomeData());
    }

    @GetMapping("/list")
    public ResponseEntity<Response<Page
            <PropertyResponseDto>>> getPropertyList( @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PropertyResponseDto> properties = propertyService.getPropertyList(pageable);
        return Response.successResponse("Property with tenant id success", properties);
    }


    private Pageable createPageAble(SortBy sortBy, SortDirection sortDirection, Integer page, Integer size) {
        if (sortBy != null && sortDirection != null) {
            Sort.Direction direction = (sortDirection == SortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Sort sort;
            if (sortBy == SortBy.PRICE) {
                sort = Sort.by(direction, "rooms.basePrice");
            } else {
                sort = Sort.by(direction, "propertyName");
            }
            return PageRequest.of(page, size, sort);
        }

        return PageRequest.of(page, size);
    }


    public enum SortBy {
        PRICE, NAME
    }

    public enum SortDirection {
        ASC, DESC
    }
}
