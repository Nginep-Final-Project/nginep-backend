package com.example.nginep.reviews.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ReviewReplyDto {
    private Long id;
    private Long reviewId;
    private String tenantName;
    private String reply;
    private Instant createdAt;
}