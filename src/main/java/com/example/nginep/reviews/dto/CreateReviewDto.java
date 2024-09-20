package com.example.nginep.reviews.dto;

import lombok.Data;

@Data
public class CreateReviewDto {
    private Long bookingId;
    private Integer cleanlinessRating;
    private Integer communicationRating;
    private Integer checkInRating;
    private Integer accuracyRating;
    private Integer locationRating;
    private Integer valueRating;
    private String comment;
}