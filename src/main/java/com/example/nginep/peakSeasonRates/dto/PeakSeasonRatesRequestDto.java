package com.example.nginep.peakSeasonRates.dto;

import com.example.nginep.peakSeasonRates.entity.DateRange;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import com.example.nginep.property.entity.Property;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PeakSeasonRatesRequestDto {
    private Long id;
    private DateRange peakSeasonDates;
    private PeakSeasonRates.RateType rateType;
    private BigDecimal rateValue;
    private Long propertyId;

    public PeakSeasonRates toEntity(Property property) {
        PeakSeasonRates newPeakSeasonRate = new PeakSeasonRates();
        newPeakSeasonRate.setPeakSeasonDates(peakSeasonDates);
        newPeakSeasonRate.setRateType(rateType);
        newPeakSeasonRate.setRateValue(rateValue);
        newPeakSeasonRate.setProperty(property);
        return newPeakSeasonRate;
    }
}
