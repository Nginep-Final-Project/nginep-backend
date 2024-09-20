package com.example.nginep.bookings.repository;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByRoomId(Long roomId);

    //TODO: Add property entity dl nanti baru jalanin
//    @Query("SELECT b FROM Booking b " +
//            "JOIN Room r ON b.roomId = r.id " +
//            "JOIN Property p ON r.propertyId = p.id " +
//            "WHERE p.tenantId = :tenantId " +
//            "AND (:status IS NULL OR b.status = :status)")
//    List<Booking> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") BookingStatus status);
}