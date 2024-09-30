package com.example.nginep.bookings.repository;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByRoomId(Long roomId);

    List<Booking> findByUser(Users user);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.room r " +
            "JOIN r.property p " +
            "WHERE p.user.id = :tenantId")
    List<Booking> findByTenant(@Param("tenantId") Long tenantId);

    Optional<Booking> findByUserAndRoomAndStatus(Users user, Room room, BookingStatus status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status != :cancelledStatus " +
            "AND ((b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate) OR " +
            "(b.checkInDate = :checkInDate AND b.checkOutDate = :checkOutDate))")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkOutDate") LocalDate checkOutDate,
                                     @Param("cancelledStatus") BookingStatus cancelledStatus);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED' AND b.checkOutDate < CURRENT_DATE AND NOT EXISTS (SELECT r FROM Review r WHERE r.booking = b)")
    List<Booking> findUnreviewedBookingsForUser(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(b.finalPrice), 0) FROM Booking b " +
            "JOIN b.room r JOIN r.property p " +
            "WHERE p.user.id = :tenantId AND b.status = 'CONFIRMED'")
    BigDecimal calculateTotalEarningsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(b) FROM Booking b " +
            "JOIN b.room r JOIN r.property p " +
            "WHERE p.user.id = :tenantId AND b.status = 'CONFIRMED'")
    Long countBookingsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.room r " +
            "JOIN r.property p " +
            "WHERE p.user.id = :tenantId " +
            "AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByTenant(@Param("tenantId") Long tenantId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.room r " +
            "JOIN r.property p " +
            "WHERE p.user.id = :tenantId " +
            "AND b.status = :status " +
            "AND b.checkInDate >= :startDate " +
            "AND b.checkInDate <= :endDate")
    List<Booking> findConfirmedBookingsBetweenDatesForTenant(
            @Param("tenantId") Long tenantId,
            @Param("status") BookingStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Booking> findByRoomPropertyIdAndStatus(Long propertyId, BookingStatus status);
}