package com.example.nginep.reviews.dto;

import lombok.Data;

@Data
public class PropertyReviewSummaryDto {
    private Double averageRating;
    private Long totalReviews;
    private Double cleanlinessRating;
    private Double communicationRating;
    private Double checkInRating;
    private Double accuracyRating;
    private Double locationRating;
    private Double valueRating;
}