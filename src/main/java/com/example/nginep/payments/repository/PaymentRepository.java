package com.example.nginep.payments.repository;

import com.example.nginep.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN FETCH p.booking b " +
            "LEFT JOIN FETCH b.user u " +
            "LEFT JOIN FETCH b.room r " +
            "LEFT JOIN FETCH r.property prop " +
            "LEFT JOIN FETCH prop.user host " +
            "WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithBookingDetails(@Param("paymentId") Long paymentId);
}