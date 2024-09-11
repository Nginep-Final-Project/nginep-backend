package com.example.nginep.facility.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.facility.dto.FacilityRequestDto;
import com.example.nginep.facility.dto.FacilityResponseDto;
import com.example.nginep.facility.entity.Facility;
import com.example.nginep.facility.repository.FacilityRepository;
import com.example.nginep.facility.service.FacilityService;
import com.example.nginep.languages.dto.LangaugesResponseDto;
import com.example.nginep.languages.entity.Languages;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    private final UsersService usersService;

    public FacilityServiceImpl(FacilityRepository facilityRepository, UsersService usersService) {
        this.facilityRepository = facilityRepository;
        this.usersService = usersService;
    }

    @Override
    public FacilityResponseDto createFacility(FacilityRequestDto facilityRequestDto) {
        Users user = usersService.getDetailUserId(facilityRequestDto.getTenantId());

        Facility newFacility = facilityRepository.save(facilityRequestDto.toEntity(user));
        return mapToFacilityResponseDto(newFacility);
    }

    @Override
    public List<FacilityResponseDto> getFacilityByTenantId(Long tenantId) {
        return facilityRepository.findAllByUserId(tenantId).stream().map(this::mapToFacilityResponseDto).toList();
    }

    @Override
    public String deleteFacility(Long facilityId) {
        facilityRepository.findById(facilityId).orElseThrow(()->new NotFoundException("Facility with id: " + facilityId + " not found"));
        facilityRepository.deleteById(facilityId);
        return "Facility with id: " + facilityId + " has deleted successfully";
    }

    public FacilityResponseDto mapToFacilityResponseDto(Facility facility) {
        FacilityResponseDto response = new FacilityResponseDto();
        response.setId(facility.getId());
        response.setValue(facility.getValue());
        response.setLabel(facility.getLabel());
        response.setTenantId(facility.getUser().getId());
        return response;
    }
}
