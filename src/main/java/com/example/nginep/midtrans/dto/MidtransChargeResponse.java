package com.example.nginep.midtrans.dto;

import lombok.Data;

@Data
public class MidtransChargeResponse {
    private String statusCode;
    private String statusMessage;
    private String transactionId;
    private String orderId;
    private String grossAmount;
    private String paymentType;
    private String transactionTime;
    private String transactionStatus;
    private VaNumbers vaNumbers;

    @Data
    public static class VaNumbers {
        private String bank;
        private String vaNumber;
    }
}