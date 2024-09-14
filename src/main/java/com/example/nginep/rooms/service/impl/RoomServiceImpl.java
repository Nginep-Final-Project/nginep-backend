package com.example.nginep.rooms.service.impl;

import com.example.nginep.rooms.entity.Room;
import com.example.nginep.rooms.repository.RoomRepository;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));
    }
}