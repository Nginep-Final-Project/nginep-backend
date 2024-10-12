package com.example.nginep.propertyImages.controller;

import com.example.nginep.propertyImages.dto.PropertyImageRequestDto;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.propertyImages.service.PropertyImageService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/property-image")
@Validated
@Log
public class PropertyImageController {
    private final PropertyImageService propertyImageService;

    public PropertyImageController(PropertyImageService propertyImageService) {
        this.propertyImageService = propertyImageService;
    }

    @PostMapping
    public ResponseEntity<Response<PropertyImageResponseDto>> createPropertyImage(@RequestBody PropertyImageRequestDto propertyImageRequestDto) {
        return Response.successResponse("Create property image success", propertyImageService.createPropertyImage(propertyImageRequestDto));
    }

    @PutMapping
    public ResponseEntity<Response<String>> setThumbnailImage(@RequestBody PropertyImageRequestDto propertyImageRequestDto){
        return Response.successResponse("Update thumbnail property image success", propertyImageService.setThumbnailImage(propertyImageRequestDto));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response<List<PropertyImageResponseDto>>> getPropertyImageByPropertyId(@PathVariable Long propertyId){
        return Response.successResponse("Get property image success", propertyImageService.getPropertyImageByPropertyId(propertyId));
    }

    @DeleteMapping("/{propertyImageId}")
    public ResponseEntity<Response<String>> deletePropertyImage(@PathVariable Long propertyImageId) {
        return Response.successResponse("Delete property image success", propertyImageService.deletePropertyImage(propertyImageId));
    }
}
