package com.example.nginep.bookings.dto;

import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BookingPaymentDetailsDto {
    private Long bookingId;
    private Long roomId;
    private Long paymentId;
    private BigDecimal finalPrice;
    private PaymentStatus paymentStatus;
    private Instant expiryTime;
    private String propertyName;
    private String roomName;
    private String propertyAddress;
    private String propertyCity;
    private String propertyProvince;
    private String coverImage;
    private PaymentType paymentType;

    private String bankName;
    private String bankAccountNumber;
    private String bankHolderName;

    private String specificPaymentType;
    private String vaNumber;
    private String billKey;
    private String billerCode;
    private String qrisUrl;

}