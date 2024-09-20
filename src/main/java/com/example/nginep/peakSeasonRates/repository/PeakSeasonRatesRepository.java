package com.example.nginep.peakSeasonRates.repository;

import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeakSeasonRatesRepository extends JpaRepository<PeakSeasonRates, Long> {
    List<PeakSeasonRates> findAllByPropertyId(Long propertyId);
}
