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

    @GetMapping("/overview")
    public ResponseEntity<Response<OverviewReportDto>> getOverviewReport() {
        OverviewReportDto report = analyticsService.getOverviewReport();
        return Response.successResponse("Overview report retrieved successfully", report);
    }

    @GetMapping("/earnings/transaction")
    public ResponseEntity<Response<EarningsByTransactionDto>> getEarnings(
            @RequestParam String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        EarningsByTransactionDto earnings = analyticsService.getEarningsByTransaction(interval, startDate, endDate);
        return Response.successResponse("Earnings data retrieved successfully", earnings);
    }

    @GetMapping("/earnings/property")
    public ResponseEntity<Response<List<EarningsByPropertyDto>>> getEarningsByProperty() {
        List<EarningsByPropertyDto> earningsByProperty = analyticsService.getEarningsByProperty();
        return Response.successResponse("Earnings by property retrieved successfully", earningsByProperty);
    }

    @GetMapping("/property-availability")
    public ResponseEntity<Response<List<PropertyAvailabilityDto>>> getPropertyAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PropertyAvailabilityDto> availability = analyticsService.getPropertyAvailability(startDate, endDate);
        return Response.successResponse("Property availability data retrieved successfully", availability);
    }
}