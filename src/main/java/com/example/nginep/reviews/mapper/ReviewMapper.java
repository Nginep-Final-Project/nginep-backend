package com.example.nginep.reviews.mapper;

import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.reviews.dto.ReviewReplyDto;
import com.example.nginep.reviews.entity.Review;
import com.example.nginep.reviews.entity.ReviewReply;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto mapToReviewDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setPropertyName(review.getProperty().getPropertyName());
        dto.setFullName(review.getUser().getFullName());
        dto.setUserPicture(review.getUser().getProfilePicture());
        dto.setCleanlinessRating(review.getCleanlinessRating());
        dto.setCommunicationRating(review.getCommunicationRating());
        dto.setCheckInRating(review.getCheckInRating());
        dto.setAccuracyRating(review.getAccuracyRating());
        dto.setLocationRating(review.getLocationRating());
        dto.setValueRating(review.getValueRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setAverageRating((review.getCleanlinessRating() + review.getCommunicationRating() +
                review.getCheckInRating() + review.getAccuracyRating() +
                review.getLocationRating() + review.getValueRating()) / 6.0);

        if (review.getReviewReply() != null) {
            dto.setReply(mapToReviewReplyDto(review.getReviewReply()));
        }

        return dto;
    }

    public ReviewReplyDto mapToReviewReplyDto(ReviewReply reviewReply) {
        if (reviewReply == null) {
            return null;
        }

        ReviewReplyDto dto = new ReviewReplyDto();
        dto.setId(reviewReply.getId());
        dto.setReviewId(reviewReply.getReview().getId());
        dto.setTenantName(reviewReply.getTenant().getFullName());
        dto.setReply(reviewReply.getReply());
        dto.setCreatedAt(reviewReply.getCreatedAt());
        return dto;
    }

}