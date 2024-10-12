package com.example.nginep.reviews.service;

import com.example.nginep.reviews.dto.CreateReviewReplyDto;
import com.example.nginep.reviews.dto.ReviewReplyDto;

public interface ReviewReplyService {
    ReviewReplyDto createReviewReply(CreateReviewReplyDto createReviewReplyDto);

    ReviewReplyDto getReviewReply(Long replyId);

    void deleteReviewReply(Long replyId);
}