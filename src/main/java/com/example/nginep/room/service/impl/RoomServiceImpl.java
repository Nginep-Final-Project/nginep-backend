package com.example.nginep.room.service.impl;

import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.room.dto.RoomRequestDto;
import com.example.nginep.room.dto.RoomResponseDto;
import com.example.nginep.room.entity.Room;
import com.example.nginep.room.repository.RoomRepository;
import com.example.nginep.room.service.RoomService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final PropertyService propertyService;

    public RoomServiceImpl(RoomRepository roomRepository, PropertyService propertyService) {
        this.roomRepository = roomRepository;
        this.propertyService = propertyService;
    }


    @Override
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto) {
        Property property = propertyService.getPropertyById(roomRequestDto.getPropertyId());
        Room newRoom = roomRepository.save(roomRequestDto.toEntity(property));
        return mapToRoomResponseDto(newRoom);
    }

    @Override
    public List<RoomResponseDto> getRoomByPropertyId(Long propertyId) {
        return roomRepository.findAllByPropertyId(propertyId).stream().map(this::mapToRoomResponseDto).toList();
    }

    @Override
    public RoomResponseDto editRoom(RoomRequestDto roomRequestDto) {
        Room room = roomRepository.findById(roomRequestDto.getId()).orElseThrow(() -> new NotFoundException("Room with id: " + roomRequestDto.getId() + " not found"));
        room.setName(roomRequestDto.getName());
        room.setDescription(roomRequestDto.getDescription());
        room.setPrice(roomRequestDto.getPrice());
        room.setMaxOccupancy(roomRequestDto.getMaxOccupancy());
        Room editedRoom = roomRepository.save(room);
        return mapToRoomResponseDto(editedRoom);
    }


    @Override
    public String deleteRoom(Long roomId) {
        roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Room with id: " + roomId + " not found"));
        roomRepository.deleteById(roomId);
        return "Room with id: " + roomId + " has deleted successfully";
    }

    public RoomResponseDto mapToRoomResponseDto(Room room) {
        RoomResponseDto response = new RoomResponseDto();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setDescription(room.getDescription());
        response.setPrice(room.getPrice());
        response.setMaxOccupancy(room.getMaxOccupancy());
        response.setPropertyId(room.getProperty().getId());
        return response;
    }
}
