package com.example.nginep.analytics.service;

import com.example.nginep.analytics.dto.EarningsByPropertyDto;
import com.example.nginep.analytics.dto.EarningsByTransactionDto;
import com.example.nginep.analytics.dto.OverviewReportDto;
import com.example.nginep.analytics.dto.PropertyAvailabilityDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    BigDecimal calculateTotalEarnings(Long tenantId);

    Long calculateTotalBookings(Long tenantId);

    Long calculateTotalProperties(Long tenantId);

    OverviewReportDto getOverviewReport(Long tenantId);

    BigDecimal calculatePeakSeasonRevenueDifference(Long tenantId);

    EarningsByTransactionDto getEarningsByTransaction(Long tenantId, String interval, LocalDate startDate, LocalDate endDate);

    List<EarningsByPropertyDto> getEarningsByProperty(Long tenantId);

    List<PropertyAvailabilityDto> getPropertyAvailability(Long tenantId, LocalDate startDate, LocalDate endDate);
}