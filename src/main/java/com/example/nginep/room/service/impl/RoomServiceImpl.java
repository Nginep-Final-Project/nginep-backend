package com.example.nginep.room.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.room.entity.Room;
import com.example.nginep.room.repository.RoomRepository;
import com.example.nginep.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));
    }
}