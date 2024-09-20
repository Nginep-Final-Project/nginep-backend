package com.example.nginep.bookings.entity;

import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.reviews.entity.Review;

import com.example.nginep.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.nginep.rooms.entity.Room;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookings_id_gen")
    @SequenceGenerator(name = "bookings_id_gen", sequenceName = "bookings_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "num_guests", nullable = false)
    private Integer numGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "user_message")
    private String userMessage;

    @JsonIgnore
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

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