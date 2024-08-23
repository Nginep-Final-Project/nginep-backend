package com.example.nginep.payments.entity;

import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_id_gen")
    @SequenceGenerator(name = "payments_id_gen", sequenceName = "payments_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "proof_of_payment")
    private String proofOfPayment;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}