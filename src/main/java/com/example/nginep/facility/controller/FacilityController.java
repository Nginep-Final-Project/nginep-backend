package com.example.nginep.facility.controller;

import com.example.nginep.facility.dto.FacilityRequestDto;
import com.example.nginep.facility.dto.FacilityResponseDto;
import com.example.nginep.facility.service.FacilityService;
import com.example.nginep.languages.dto.LangaugesRequestDto;
import com.example.nginep.languages.dto.LangaugesResponseDto;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facility")
@Validated
@Log
public class FacilityController {
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PostMapping
    public ResponseEntity<Response<FacilityResponseDto>> createFacility(@RequestBody FacilityRequestDto facilityRequestDto) {
        return Response.successResponse("Create facility success", facilityService.createFacility(facilityRequestDto));
    }

    @PutMapping
    public ResponseEntity<Response<FacilityResponseDto>> editFacility(@RequestBody FacilityRequestDto facilityRequestDto) {
        return Response.successResponse("Edit facility success", facilityService.editFacility(facilityRequestDto));
    }

    @GetMapping
    public ResponseEntity<Response<List<FacilityResponseDto>>> getFacilityByTenantId() {
        return Response.successResponse("Get list facility success" , facilityService.getFacilityByTenantId());
    }

    @DeleteMapping("/{facilityId}")
    public ResponseEntity<Response<String>> deleteFacility(@PathVariable Long facilityId) {
        return Response.successResponse("Delete facility success", facilityService.deleteFacility(facilityId));
    }
}
