package com.example.nginep.reviews.dto;

import lombok.Data;

@Data
public class CreateReviewReplyDto {
    private Long reviewId;
    private Long tenantId;
    private String reply;
}