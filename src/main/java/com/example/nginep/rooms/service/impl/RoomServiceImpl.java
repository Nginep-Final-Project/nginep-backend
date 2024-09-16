package com.example.nginep.rooms.service.impl;

import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.rooms.dto.RoomResponseDto;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.rooms.repository.RoomRepository;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final PropertyService propertyService;

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));
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
        room.setBasePrice(roomRequestDto.getBasePrice());
        room.setMaxGuests(roomRequestDto.getMaxGuests());
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
        response.setBasePrice(room.getBasePrice());
        response.setMaxGuests(room.getMaxGuests());
        response.setPropertyId(room.getProperty().getId());
        return response;
    }
}