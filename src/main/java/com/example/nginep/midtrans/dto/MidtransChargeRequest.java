package com.example.nginep.midtrans.dto;

import lombok.Data;

@Data
public class MidtransChargeRequest {
    private String orderId;
    private Long amount;
}