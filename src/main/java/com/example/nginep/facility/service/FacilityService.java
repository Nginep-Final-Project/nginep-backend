package com.example.nginep.facility.service;


import com.example.nginep.facility.dto.FacilityRequestDto;
import com.example.nginep.facility.dto.FacilityResponseDto;

import java.util.List;

public interface FacilityService {
    FacilityResponseDto createFacility(FacilityRequestDto facilityRequestDto);
    List<FacilityResponseDto> getFacilityByTenantId(Long tenantId);
    String deleteFacility(Long facilityId);
}
