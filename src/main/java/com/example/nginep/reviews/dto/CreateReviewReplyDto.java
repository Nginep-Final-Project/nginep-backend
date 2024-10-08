package com.example.nginep.reviews.dto;

import lombok.Data;

@Data
public class CreateReviewReplyDto {
    private Long reviewId;
    private String reply;
}