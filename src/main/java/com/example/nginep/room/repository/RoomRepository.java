package com.example.nginep.room.repository;

import com.example.nginep.room.dto.RoomResponseDto;
import com.example.nginep.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByPropertyId(Long propertyId);
}
