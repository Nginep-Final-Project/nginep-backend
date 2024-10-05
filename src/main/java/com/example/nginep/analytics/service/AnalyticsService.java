package com.example.nginep.analytics.service;

import com.example.nginep.analytics.dto.EarningsByPropertyDto;
import com.example.nginep.analytics.dto.EarningsByTransactionDto;
import com.example.nginep.analytics.dto.OverviewReportDto;
import com.example.nginep.analytics.dto.PropertyAvailabilityDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    BigDecimal calculateTotalEarnings();

    Long calculateTotalBookings();

    Long calculateTotalProperties();

    OverviewReportDto getOverviewReport();

    BigDecimal calculatePeakSeasonRevenueDifference();

    EarningsByTransactionDto getEarningsByTransaction(String interval, LocalDate startDate, LocalDate endDate);

    List<EarningsByPropertyDto> getEarningsByProperty();

    List<PropertyAvailabilityDto> getPropertyAvailability(LocalDate startDate, LocalDate endDate);
}