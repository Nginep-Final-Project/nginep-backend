package com.example.nginep.room.controller;

import com.example.nginep.response.Response;
import com.example.nginep.room.dto.RoomRequestDto;
import com.example.nginep.room.dto.RoomResponseDto;
import com.example.nginep.room.service.RoomService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@Log
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Response<RoomResponseDto>> createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        return Response.successResponse("Create room success", roomService.createRoom(roomRequestDto));
    }

    @PutMapping
    public ResponseEntity<Response<RoomResponseDto>> editRoom(@RequestBody RoomRequestDto roomRequestDto) {
        return Response.successResponse("Edit room success", roomService.editRoom(roomRequestDto));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response<List<RoomResponseDto>>> getRoomByPropertyId(@PathVariable Long propertyId) {
        return Response.successResponse("Get all room success", roomService.getRoomByPropertyId(propertyId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Response<String>> deleteRoom(@PathVariable Long roomId) {
        return Response.successResponse("Delete room success", roomService.deleteRoom(roomId));
    }
}
