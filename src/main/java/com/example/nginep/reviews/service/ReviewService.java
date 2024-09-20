package com.example.nginep.reviews.service;

import com.example.nginep.reviews.dto.CreateReviewDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.reviews.entity.Review;

import java.util.List;

public interface ReviewService {
    PropertyReviewSummaryDto getPropertyReviewSummary(Long propertyId);

    List<ReviewDto> getTopReviewsByPropertyId(Long propertyId, int limit);

    ReviewDto createReview(CreateReviewDto createReviewDto);

    List<ReviewDto> getUserReviews(Long userId);

    Review findReviewById(Long reviewId);
}