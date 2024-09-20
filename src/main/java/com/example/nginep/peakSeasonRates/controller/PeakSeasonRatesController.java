package com.example.nginep.peakSeasonRates.controller;

import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesRequestDto;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import com.example.nginep.peakSeasonRates.service.PeakSeasonRatesService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/peak-season-rates")
@Validated
@Log
public class PeakSeasonRatesController {
    private final PeakSeasonRatesService peakSeasonRatesService;

    public PeakSeasonRatesController(PeakSeasonRatesService peakSeasonRatesService) {
        this.peakSeasonRatesService = peakSeasonRatesService;
    }

    @PostMapping
    public ResponseEntity<Response<PeakSeasonRatesResponseDto>> createPeakSeasonRates(@RequestBody PeakSeasonRatesRequestDto peakSeasonRatesRequestDto) {
        return Response.successResponse("Create peak season rates success", peakSeasonRatesService.createPeakSeasonRates(peakSeasonRatesRequestDto));
    }

    @PutMapping
    public ResponseEntity<Response<PeakSeasonRatesResponseDto>> editPeakSeasonRates(@RequestBody PeakSeasonRatesRequestDto peakSeasonRatesRequestDto) {
        return Response.successResponse("Edit peak season rates success", peakSeasonRatesService.editPeakSeasonRates(peakSeasonRatesRequestDto));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response<List<PeakSeasonRatesResponseDto>>> getPeakSeasonRatesByPropertyId(@PathVariable Long propertyId) {
        return Response.successResponse("Get peak season rates by property id success", peakSeasonRatesService.getPeakSeasonRatesByPropertyId(propertyId));
    }

    @GetMapping("/{peakSeasonRatesId}")
    public ResponseEntity<Response<PeakSeasonRates>> getPeakSeasonRatesById(@PathVariable Long peakSeasonRatesId) {
        return Response.successResponse("Get peak season rates by id success", peakSeasonRatesService.getDetailPeakSeasonRatesById(peakSeasonRatesId));
    }

    @DeleteMapping("/{peakSeasonRatesId}")
    public ResponseEntity<Response<String>> deletePeakSeasonRatesById(@PathVariable Long peakSeasonRatesId) {
        return Response.successResponse("Delete peak season rates by id success", peakSeasonRatesService.deletePeakSeasonRates(peakSeasonRatesId));
    }
}
