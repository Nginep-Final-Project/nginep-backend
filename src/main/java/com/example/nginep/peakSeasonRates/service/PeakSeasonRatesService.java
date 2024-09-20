package com.example.nginep.peakSeasonRates.service;

import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesRequestDto;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;

import java.util.List;

public interface PeakSeasonRatesService {
    PeakSeasonRatesResponseDto createPeakSeasonRates(PeakSeasonRatesRequestDto peakSeasonRatesRequestDto);

    PeakSeasonRatesResponseDto editPeakSeasonRates(PeakSeasonRatesRequestDto peakSeasonRatesRequestDto);

    List<PeakSeasonRatesResponseDto> getPeakSeasonRatesByPropertyId(Long propertyId);

    PeakSeasonRates getDetailPeakSeasonRatesById(Long peakSeasonRatesId);

    String deletePeakSeasonRates(Long peakSeasonRatesId);
}
