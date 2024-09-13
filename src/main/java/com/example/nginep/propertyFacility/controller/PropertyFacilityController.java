package com.example.nginep.propertyFacility.controller;

import com.example.nginep.propertyFacility.dto.PropertyFacilityRequestDto;
import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyFacility.service.PropertyFacilityService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/property-facility")
@Validated
@Log
public class PropertyFacilityController {
    private final PropertyFacilityService propertyFacilityService;

    public PropertyFacilityController(PropertyFacilityService propertyFacilityService) {
        this.propertyFacilityService = propertyFacilityService;
    }

    @PostMapping
    public ResponseEntity<Response<PropertyFacilityResponseDto>> createPropertyFacility(@RequestBody PropertyFacilityRequestDto propertyFacilityRequestDto) {
        return Response.successResponse("Create property facility success", propertyFacilityService.createPropertyFacility(propertyFacilityRequestDto));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response<List<PropertyFacilityResponseDto>>> getFacilityByPropertyId(@PathVariable Long propertyId){
        return Response.successResponse("Get Facility By property id: " + propertyId, propertyFacilityService.getFacilityByPropertyId(propertyId));
    }

    @DeleteMapping("/{propertyFacilityId}")
    public ResponseEntity<Response<String>> deletePropertyFacility(@PathVariable Long propertyFacilityId) {
        return Response.successResponse("Delete property facility with id: " + propertyFacilityId + " success", propertyFacilityService.deletePropertyFacility(propertyFacilityId));
    }
}
