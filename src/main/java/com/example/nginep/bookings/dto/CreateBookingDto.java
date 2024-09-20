package com.example.nginep.bookings.dto;

import com.example.nginep.payments.enums.PaymentType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingDto {
    private Long roomId;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numGuests;
    private PaymentType paymentMethod;
    private String userMessage;
    private String bank;
}