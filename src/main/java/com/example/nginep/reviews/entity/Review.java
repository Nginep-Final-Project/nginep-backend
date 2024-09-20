package com.example.nginep.reviews.entity;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.property.entity.Property;
import com.example.nginep.users.entity.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviews_id_gen")
    @SequenceGenerator(name = "reviews_id_gen", sequenceName = "reviews_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "cleanliness_rating", nullable = false)
    private Integer cleanlinessRating;

    @Column(name = "communication_rating", nullable = false)
    private Integer communicationRating;

    @Column(name = "check_in_rating", nullable = false)
    private Integer checkInRating;

    @Column(name = "accuracy_rating", nullable = false)
    private Integer accuracyRating;

    @Column(name = "location_rating", nullable = false)
    private Integer locationRating;

    @Column(name = "value_rating", nullable = false)
    private Integer valueRating;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

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