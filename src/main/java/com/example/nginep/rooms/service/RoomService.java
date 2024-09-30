package com.example.nginep.rooms.service;

import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.rooms.dto.RoomResponseDto;
import com.example.nginep.rooms.dto.SearchAvailableRoomRequestDto;
import com.example.nginep.rooms.entity.Room;

import java.util.List;

public interface RoomService {
    Room getRoomById(Long id);

    RoomResponseDto createRoom(RoomRequestDto roomRequestDto);

    List<RoomResponseDto> getRoomByPropertyId(Long propertyId);

    RoomResponseDto editRoom(RoomRequestDto roomRequestDto);

    String deleteRoom(Long roomId);

    List<Room> searchRoomAvailable(SearchAvailableRoomRequestDto searchAvailableRoomRequestDto);
}