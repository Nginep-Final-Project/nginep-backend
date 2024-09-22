package com.example.nginep.property.controller;

import com.example.nginep.property.dto.HomeResponseDto;
import com.example.nginep.property.dto.PropertyRequestDto;
import com.example.nginep.property.dto.PropertyResponseDto;
import com.example.nginep.property.dto.SearchResponseDto;
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
    public ResponseEntity<Response<PropertyResponseDto>> getDetailProperty(@PathVariable Long propertyId) {
        return Response.successResponse("Get detail property success", propertyService.getDetailProperty(propertyId));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Response<String>> deleteProperty(@PathVariable Long propertyId) {
        return Response.successResponse("Delete property success", propertyService.deleteProperty(propertyId));
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
            @RequestParam(defaultValue = "20") int size) {

        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageableWithSort = PageRequest.of(page, size, sort);


        return Response.successResponse("All events fetched", propertyService.getAllProperty(pageableWithSort,
                propertyName, propertyCategory, propertyCity, checkinDate, checkoutDate, totalGuests));
    }

    @GetMapping("/home")
    public ResponseEntity<Response<HomeResponseDto>> getHomeData(){
        return Response.successResponse("Get home data success", propertyService.getHomeData());
    }


    private Sort createSort(SortBy sortBy, SortDirection sortDirection) {
        Sort.Direction direction = (sortDirection == SortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if (sortBy == SortBy.PRICE) {
            return Sort.by(direction, "rooms.price");
        } else {
            return Sort.by(direction, "propertyName");
        }
    }



    public enum SortBy {
        PRICE, NAME
    }

    public enum SortDirection {
        ASC, DESC
    }
}
