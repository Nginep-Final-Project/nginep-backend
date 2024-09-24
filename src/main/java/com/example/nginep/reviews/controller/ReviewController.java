package com.example.nginep.reviews.controller;

import com.example.nginep.reviews.dto.CreateReviewDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.reviews.service.ReviewService;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/property/{propertyId}/summary")
    public ResponseEntity<Response<PropertyReviewSummaryDto>> getPropertyReviewSummary(@PathVariable Long propertyId) {
        PropertyReviewSummaryDto summary = reviewService.getPropertyReviewSummary(propertyId);
        return Response.successResponse("Property review summary retrieved successfully", summary);
    }

    @GetMapping("/property/{propertyId}/top-reviews")
    public ResponseEntity<Response<List<ReviewDto>>> getTopReviews(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "7") int limit) {
        List<ReviewDto> topReviews = reviewService.getTopReviewsByPropertyId(propertyId, limit);
        return Response.successResponse("Top reviews retrieved successfully", topReviews);
    }

    @PostMapping
    public ResponseEntity<Response<ReviewDto>> createReview(@RequestBody CreateReviewDto createReviewDto) {
        ReviewDto createdReview = reviewService.createReview(createReviewDto);
        return Response.successResponse("Review created successfully", createdReview);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response<List<ReviewDto>>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDto> userReviews = reviewService.getUserReviews(userId);
        return Response.successResponse("User reviews retrieved successfully", userReviews);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Response<List<ReviewDto>>> getReviewsByPropertyId(@PathVariable Long propertyId) {
        List<ReviewDto> reviews = reviewService.getReviewsByPropertyId(propertyId);
        return Response.successResponse("Reviews retrieved successfully", reviews);
    }
}