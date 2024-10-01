package com.example.nginep.analytics.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewReportDto {
    private BigDecimal totalEarnings;
    private Long totalBookings;
    private Long totalProperties;
    private BigDecimal peakSeasonRevenueDifference;
}
