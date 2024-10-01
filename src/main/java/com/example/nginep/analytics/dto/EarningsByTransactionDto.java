package com.example.nginep.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EarningsByTransactionDto {
    private BigDecimal totalEarnings;
    private List<EarningDataPoint> earningsData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarningDataPoint {
        private String date;
        private BigDecimal amount;

    }
}