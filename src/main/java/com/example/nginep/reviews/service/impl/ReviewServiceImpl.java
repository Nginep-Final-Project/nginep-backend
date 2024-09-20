package com.example.nginep.reviews.service.impl;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.reviews.dto.CreateReviewDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.reviews.entity.Review;
import com.example.nginep.reviews.entity.ReviewReply;
import com.example.nginep.reviews.mapper.ReviewMapper;
import com.example.nginep.reviews.repository.ReviewRepository;
import com.example.nginep.reviews.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final ReviewMapper reviewMapper;

    @Override
    public PropertyReviewSummaryDto getPropertyReviewSummary(Long propertyId) {
        Double averageRating = reviewRepository.findAverageOverallRatingByPropertyId(propertyId);
        List<Object[]> averageRatings = reviewRepository.findAverageEachRatingByPropertyId(propertyId);
        Long totalReviews = reviewRepository.countReviewsByPropertyId(propertyId);

        PropertyReviewSummaryDto summary = new PropertyReviewSummaryDto();
        summary.setAverageRating(averageRating != null ? averageRating : 0.0);
        summary.setTotalReviews(totalReviews);

        if (!averageRatings.isEmpty()) {
            Object[] ratings = averageRatings.getFirst();
            summary.setCleanlinessRating((Double) ratings[0]);
            summary.setCommunicationRating((Double) ratings[1]);
            summary.setCheckInRating((Double) ratings[2]);
            summary.setAccuracyRating((Double) ratings[3]);
            summary.setLocationRating((Double) ratings[4]);
            summary.setValueRating((Double) ratings[5]);
        }

        return summary;
    }

    @Override
    public List<ReviewDto> getTopReviewsByPropertyId(Long propertyId, int limit) {
        List<Review> topReviews = reviewRepository.findTopReviewsByPropertyId(propertyId, PageRequest.of(0, limit));
        return topReviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .toList();
    }


    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewDto createReviewDto) {
        Booking booking = bookingService.findBookingById(createReviewDto.getBookingId());

        Review review = new Review();
        review.setBooking(booking);
        review.setUser(booking.getUser());
        review.setProperty(booking.getRoom().getProperty());
        review.setCleanlinessRating(createReviewDto.getCleanlinessRating());
        review.setCommunicationRating(createReviewDto.getCommunicationRating());
        review.setCheckInRating(createReviewDto.getCheckInRating());
        review.setAccuracyRating(createReviewDto.getAccuracyRating());
        review.setLocationRating(createReviewDto.getLocationRating());
        review.setValueRating(createReviewDto.getValueRating());
        review.setComment(createReviewDto.getComment());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.mapToReviewDto(savedReview);
    }

    @Override
    public List<ReviewDto> getUserReviews(Long userId) {
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        return userReviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .toList();
    }

    @Override
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException("Review not found with id: " + reviewId));
    }

}