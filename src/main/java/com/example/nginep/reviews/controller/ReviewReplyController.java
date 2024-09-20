package com.example.nginep.reviews.controller;

import com.example.nginep.reviews.dto.CreateReviewReplyDto;
import com.example.nginep.reviews.dto.ReviewReplyDto;
import com.example.nginep.reviews.service.ReviewReplyService;
import com.example.nginep.reviews.service.ReviewService;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review-replies")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    @PostMapping
    public ResponseEntity<Response<ReviewReplyDto>> createReviewReply(@RequestBody CreateReviewReplyDto createReviewReplyDto) {
        ReviewReplyDto createdReply = reviewReplyService.createReviewReply(createReviewReplyDto);
        return Response.successResponse("Review reply created successfully", createdReply);
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<Response<ReviewReplyDto>> getReviewReply(@PathVariable Long replyId) {
        ReviewReplyDto reviewReply = reviewReplyService.getReviewReply(replyId);
        return Response.successResponse("Review reply retrieved successfully", reviewReply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Response<Object>> deleteReviewReply(@PathVariable Long replyId) {
        reviewReplyService.deleteReviewReply(replyId);
        return Response.successResponse("Review reply deleted successfully");
    }
}