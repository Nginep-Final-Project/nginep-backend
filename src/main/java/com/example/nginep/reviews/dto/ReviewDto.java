package com.example.nginep.reviews.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ReviewDto {
    private Long id;
    private Long bookingId;
    private String propertyName;
    private String fullName;
    private Integer cleanlinessRating;
    private Integer communicationRating;
    private Integer checkInRating;
    private Integer accuracyRating;
    private Integer locationRating;
    private Integer valueRating;
    private String comment;
    private Double averageRating;
    private Instant createdAt;
}