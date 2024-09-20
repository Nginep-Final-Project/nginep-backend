package com.example.nginep.facility.service.impl;

import com.example.nginep.auth.helpers.Claims;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.facility.dto.FacilityRequestDto;
import com.example.nginep.facility.dto.FacilityResponseDto;
import com.example.nginep.facility.entity.Facility;
import com.example.nginep.facility.repository.FacilityRepository;
import com.example.nginep.facility.service.FacilityService;
import com.example.nginep.languages.dto.LangaugesResponseDto;
import com.example.nginep.languages.entity.Languages;
import com.example.nginep.users.dto.UsersResponseDto;
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
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        Users user = usersService.getDetailUserByEmail(email);

        Facility newFacility = facilityRepository.save(facilityRequestDto.toEntity(user));
        return mapToFacilityResponseDto(newFacility);
    }

    @Override
    public FacilityResponseDto editFacility(FacilityRequestDto facilityRequestDto) {
        Facility facility = facilityRepository.findById(facilityRequestDto.getId())
                .orElseThrow(()->new NotFoundException("Facility with id: " + facilityRequestDto.getId() + " not found"));
        facility.setValue(facilityRequestDto.getValue().trim().toLowerCase().replace(" ", "-"));
        facility.setLabel(facilityRequestDto.getValue());
        Facility updatedFacility = facilityRepository.save(facility);
        return mapToFacilityResponseDto(updatedFacility);
    }

    @Override
    public List<FacilityResponseDto> getFacilityByTenantId() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        Users user = usersService.getDetailUserByEmail(email);
        return facilityRepository.findAllByUserId(user.getId()).stream().map(this::mapToFacilityResponseDto).toList();
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
        return response;
    }
}
