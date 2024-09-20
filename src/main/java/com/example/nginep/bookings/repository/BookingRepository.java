package com.example.nginep.bookings.repository;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.room.entity.Room;
import com.example.nginep.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(Users user);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.room r " +
            "JOIN r.property p " +
            "WHERE p.tenant.id = :tenantId")
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

}