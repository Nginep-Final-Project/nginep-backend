package com.example.nginep.rooms.repository;

import com.example.nginep.rooms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByPropertyId(Long propertyId);
}