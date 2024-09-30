package com.example.nginep.rooms.repository;

import com.example.nginep.rooms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByPropertyId(Long propertyId);

    @Query("SELECT r ,(r.totalRoom - COUNT(b.id)) " +
            "FROM Room r LEFT JOIN r.bookings b " +
            "ON b.room.id = r.id AND b.checkInDate <= :endDate AND b.checkOutDate >= :startDate " +
            "AND b.status IN ('CONFIRMED', 'NOT_AVAILABLE', 'AWAITING_CONFIRMATION') " +
            "WHERE r.maxGuests >= :totalGuests AND r.property.id = :propertyId " +
            "GROUP BY r " +
            "HAVING (r.totalRoom - COUNT(b.id)) > 0")
    List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("totalGuests") Integer totalGuests,
                                                      @Param("propertyId") Long propertyId);
}