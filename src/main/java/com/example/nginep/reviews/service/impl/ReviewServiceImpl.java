package com.example.nginep.reviews.service.impl;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.reviews.dto.CreateReviewDto;
import com.example.nginep.reviews.dto.PropertyReviewSummaryDto;
import com.example.nginep.reviews.dto.ReviewDto;
import com.example.nginep.reviews.entity.Review;
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
        return topReviews.stream().map(this::mapToReviewDto).collect(Collectors.toList());
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
        return mapToReviewDto(savedReview);
    }

    @Override
    public List<ReviewDto> getUserReviews(Long userId) {
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        return userReviews.stream().map(this::mapToReviewDto).collect(Collectors.toList());
    }

    private ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setPropertyName(review.getProperty().getPropertyName());
        dto.setFullName(review.getUser().getFullName());
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
        return dto;
    }

}