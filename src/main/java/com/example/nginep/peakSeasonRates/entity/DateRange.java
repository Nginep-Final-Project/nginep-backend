package com.example.nginep.peakSeasonRates.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;

@Data
@Embeddable
public class DateRange {
    private LocalDate from;
    private LocalDate to;
}
