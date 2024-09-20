package com.example.nginep.peakSeasonRates.dto;

import com.example.nginep.peakSeasonRates.entity.DateRange;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PeakSeasonRatesResponseDto {
    private Long id;
    private DateRange peakSeasonDates;
    private PeakSeasonRates.RateType rateType;
    private BigDecimal rateValue;
}
