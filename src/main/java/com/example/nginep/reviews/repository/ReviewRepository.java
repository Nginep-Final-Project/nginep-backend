package com.example.nginep.reviews.repository;

import com.example.nginep.reviews.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG((r.cleanlinessRating + r.communicationRating + r.checkInRating + r.accuracyRating + r.locationRating + r.valueRating) / 6.0) FROM Review r WHERE r.property.id = :propertyId")
    Double findAverageOverallRatingByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT AVG(r.cleanlinessRating) as cleanliness, AVG(r.communicationRating) as communication, " +
            "AVG(r.checkInRating) as checkIn, AVG(r.accuracyRating) as accuracy, " +
            "AVG(r.locationRating) as location, AVG(r.valueRating) as value " +
            "FROM Review r WHERE r.property.id = :propertyId")
    List<Object[]> findAverageEachRatingByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT r FROM Review r WHERE r.property.id = :propertyId " +
            "ORDER BY (r.cleanlinessRating + r.communicationRating + r.checkInRating + r.accuracyRating + r.locationRating + r.valueRating) / 6.0 DESC")
    List<Review> findTopReviewsByPropertyId(@Param("propertyId") Long propertyId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.property.id = :propertyId")
    Long countReviewsByPropertyId(@Param("propertyId") Long propertyId);

    List<Review> findByUserId(Long userId);

    List<Review> findByPropertyId(Long propertyId);
}