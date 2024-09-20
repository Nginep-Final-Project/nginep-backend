package com.example.nginep.peakSeasonRates.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesRequestDto;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import com.example.nginep.peakSeasonRates.repository.PeakSeasonRatesRepository;
import com.example.nginep.peakSeasonRates.service.PeakSeasonRatesService;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class PeakSeasonRatesServiceImpl implements PeakSeasonRatesService {
    private final PeakSeasonRatesRepository peakSeasonRatesRepository;
    private final PropertyService propertyService;

    public PeakSeasonRatesServiceImpl(PeakSeasonRatesRepository peakSeasonRatesRepository, PropertyService propertyService) {
        this.peakSeasonRatesRepository = peakSeasonRatesRepository;
        this.propertyService = propertyService;
    }

    @Override
    public PeakSeasonRatesResponseDto createPeakSeasonRates(PeakSeasonRatesRequestDto peakSeasonRatesRequestDto) {
        Property property = propertyService.getPropertyById(peakSeasonRatesRequestDto.getPropertyId());
        PeakSeasonRates newPeakSeasonRates = peakSeasonRatesRepository.save(peakSeasonRatesRequestDto.toEntity(property));
        return mapToPeakSeasonRatesResponseDto(newPeakSeasonRates);
    }

    @Override
    public PeakSeasonRatesResponseDto editPeakSeasonRates(PeakSeasonRatesRequestDto peakSeasonRatesRequestDto) {
        PeakSeasonRates peakSeasonRates = peakSeasonRatesRepository.findById(peakSeasonRatesRequestDto.getId())
                .orElseThrow(() -> new NotFoundException("Peak season rates with id: " + peakSeasonRatesRequestDto.getId() + " not found"));
        peakSeasonRates.setPeakSeasonDates(peakSeasonRatesRequestDto.getPeakSeasonDates());
        peakSeasonRates.setRateType(peakSeasonRatesRequestDto.getRateType());
        peakSeasonRates.setRateValue(peakSeasonRatesRequestDto.getRateValue());
        PeakSeasonRates updatedPeakSeasonRates = peakSeasonRatesRepository.save(peakSeasonRates);
        return mapToPeakSeasonRatesResponseDto(updatedPeakSeasonRates);
    }

    @Override
    public List<PeakSeasonRatesResponseDto> getPeakSeasonRatesByPropertyId(Long propertyId) {
        return peakSeasonRatesRepository.findAllByPropertyId(propertyId).stream().map(this::mapToPeakSeasonRatesResponseDto).toList();
    }

    @Override
    public PeakSeasonRates getDetailPeakSeasonRatesById(Long peakSeasonRatesId) {
        return peakSeasonRatesRepository.findById(peakSeasonRatesId)
                .orElseThrow(() -> new NotFoundException("Peak season rates with id: " + peakSeasonRatesId + " not found"));
    }

    @Override
    public String deletePeakSeasonRates(Long peakSeasonRatesId) {
        getDetailPeakSeasonRatesById(peakSeasonRatesId);
        peakSeasonRatesRepository.deleteById(peakSeasonRatesId);
        return "Delete peak season rates with id: " + peakSeasonRatesId + " success";
    }

    public PeakSeasonRatesResponseDto mapToPeakSeasonRatesResponseDto(PeakSeasonRates peakSeasonRates) {
        PeakSeasonRatesResponseDto response = new PeakSeasonRatesResponseDto();
        response.setId(peakSeasonRates.getId());
        response.setPeakSeasonDates(peakSeasonRates.getPeakSeasonDates());
        response.setRateType(peakSeasonRates.getRateType());
        response.setRateValue(peakSeasonRates.getRateValue());
        return response;
    }
}
