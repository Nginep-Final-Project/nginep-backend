package com.example.nginep.room.service;

import com.example.nginep.room.dto.RoomRequestDto;
import com.example.nginep.room.dto.RoomResponseDto;

import java.util.List;

public interface RoomService {
    RoomResponseDto createRoom(RoomRequestDto roomRequestDto);

    List<RoomResponseDto> getRoomByPropertyId(Long propertyId);

    RoomResponseDto editRoom(RoomRequestDto roomRequestDto);

    String deleteRoom(Long roomId);
}
