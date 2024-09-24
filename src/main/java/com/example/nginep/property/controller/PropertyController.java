package com.example.nginep.property.controller;

import com.example.nginep.property.dto.PropertyRequestDto;
import com.example.nginep.property.dto.PropertyResponseDto;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Response<Property>> getDetailProperty(@PathVariable Long propertyId) {
        return Response.successResponse("Get detail property success", propertyService.getPropertyById(propertyId));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Response<String>> deleteProperty(@PathVariable Long propertyId){
        return Response.successResponse("Delete property success", propertyService.deleteProperty(propertyId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Response<List
            <PropertyResponseDto>>> getPropertiesByTenantId(@PathVariable Long tenantId) {
        List<PropertyResponseDto> properties = propertyService.GetPropertyByTenantId(tenantId);
        return Response.successResponse("Properties retrieved successfully", properties);
    }
}
