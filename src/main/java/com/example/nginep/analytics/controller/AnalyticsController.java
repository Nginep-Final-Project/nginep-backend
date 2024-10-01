package com.example.nginep.analytics.controller;

import com.example.nginep.analytics.dto.EarningsByPropertyDto;
import com.example.nginep.analytics.dto.EarningsByTransactionDto;
import com.example.nginep.analytics.dto.OverviewReportDto;
import com.example.nginep.analytics.dto.PropertyAvailabilityDto;
import com.example.nginep.analytics.service.AnalyticsService;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview/{tenantId}")
    public ResponseEntity<Response<OverviewReportDto>> getOverviewReport(@PathVariable Long tenantId) {
        OverviewReportDto report = analyticsService.getOverviewReport(tenantId);
        return Response.successResponse("Overview report retrieved successfully", report);
    }

    @GetMapping("/earnings/transaction/{tenantId}")
    public ResponseEntity<Response<EarningsByTransactionDto>> getEarnings(
            @PathVariable Long tenantId,
            @RequestParam String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        EarningsByTransactionDto earnings = analyticsService.getEarningsByTransaction(tenantId, interval, startDate, endDate);
        return Response.successResponse("Earnings data retrieved successfully", earnings);
    }

    @GetMapping("/earnings/property/{tenantId}")
    public ResponseEntity<Response<List<EarningsByPropertyDto>>> getEarningsByProperty(@PathVariable Long tenantId) {
        List<EarningsByPropertyDto> earningsByProperty = analyticsService.getEarningsByProperty(tenantId);
        return Response.successResponse("Earnings by property retrieved successfully", earningsByProperty);
    }

    @GetMapping("/property-availability/{tenantId}")
    public ResponseEntity<Response<List<PropertyAvailabilityDto>>> getPropertyAvailability(
            @PathVariable Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PropertyAvailabilityDto> availability = analyticsService.getPropertyAvailability(tenantId, startDate, endDate);
        return Response.successResponse("Property availability data retrieved successfully", availability);
    }
}