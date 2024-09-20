package com.example.nginep.bookings.dto;

import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TenantBookingsDto {
    private Long bookingId;
    private Long roomId;
    private Long paymentId;
    private String propertyName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestName;
    private Integer numGuests;
    private String roomName;
    private BigDecimal finalPrice;
    private BookingStatus status;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String proofOfPayment;
    private String propertyCoverImage;
}
