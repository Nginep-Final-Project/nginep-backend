package com.example.nginep.payments.dto;

import com.example.nginep.payments.enums.PaymentType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
public class CreateMidtransPaymentDto {
    @NotNull
    private Long bookingId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentType paymentType;
}
