package com.example.nginep.reviews.service.impl;

import com.example.nginep.reviews.dto.CreateReviewReplyDto;
import com.example.nginep.reviews.dto.ReviewReplyDto;
import com.example.nginep.reviews.entity.Review;
import com.example.nginep.reviews.entity.ReviewReply;
import com.example.nginep.reviews.mapper.ReviewMapper;
import com.example.nginep.reviews.repository.ReviewReplyRepository;
import com.example.nginep.reviews.service.ReviewReplyService;
import com.example.nginep.reviews.service.ReviewService;
import com.example.nginep.users.entity.Users;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.users.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ReviewReplyServiceImpl implements ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewService reviewService;
    private final UsersService userService;
    private final ReviewMapper reviewMapper;

    public ReviewReplyServiceImpl(ReviewReplyRepository reviewReplyRepository, ReviewService reviewService, UsersService userService, ReviewMapper reviewMapper) {
        this.reviewReplyRepository = reviewReplyRepository;
        this.reviewService = reviewService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewReplyDto createReviewReply(CreateReviewReplyDto createReviewReplyDto) {
        Review review = reviewService.findReviewById(createReviewReplyDto.getReviewId());
        Users tenant = userService.getDetailUserId(createReviewReplyDto.getTenantId());

        ReviewReply reviewReply = new ReviewReply();
        reviewReply.setReview(review);
        reviewReply.setTenant(tenant);
        reviewReply.setReply(createReviewReplyDto.getReply());

        ReviewReply savedReply = reviewReplyRepository.save(reviewReply);
        return reviewMapper.mapToReviewReplyDto(savedReply);
    }

    @Override
    public ReviewReplyDto getReviewReply(Long replyId) {
        ReviewReply reviewReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("Review reply not found"));
        return reviewMapper.mapToReviewReplyDto(reviewReply);
    }

    @Override
    @Transactional
    public void deleteReviewReply(Long replyId) {
        if (!reviewReplyRepository.existsById(replyId)) {
            throw new NotFoundException("Review reply not found");
        }
        reviewReplyRepository.deleteById(replyId);
    }

}