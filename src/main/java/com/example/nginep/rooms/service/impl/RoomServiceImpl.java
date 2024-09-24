package com.example.nginep.rooms.service.impl;

import com.example.nginep.bookings.dto.CreateNotAvailableBookingDTO;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.rooms.dto.RoomRequestDto;
import com.example.nginep.rooms.dto.RoomResponseDto;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.rooms.repository.RoomRepository;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;

import com.example.nginep.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j

public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final PropertyService propertyService;
    private final BookingService bookingService;

    public RoomServiceImpl(@Lazy RoomRepository roomRepository,@Lazy PropertyService propertyService,@Lazy BookingService bookingService) {
        this.roomRepository = roomRepository;
        this.propertyService = propertyService;
        this.bookingService = bookingService;
    }


    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));
    }

    @Override
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto) {
        Property property = propertyService.getPropertyById(roomRequestDto.getPropertyId());

        Room newRoom = roomRepository.save(roomRequestDto.toEntity(property));
        for (CreateNotAvailableBookingDTO notAvailableBookingDTO: roomRequestDto.getNotAvailableDates()) {
            CreateNotAvailableBookingDTO newBooking = new CreateNotAvailableBookingDTO();
            newBooking.setUser(property.getUser());
            newBooking.setRoom(newRoom);
            newBooking.setCheckInDate(notAvailableBookingDTO.getCheckInDate());
            newBooking.setCheckOutDate(notAvailableBookingDTO.getCheckOutDate());
            newBooking.setFinalPrice(newRoom.getBasePrice());
            newBooking.setNumGuests(newRoom.getMaxGuests());
            bookingService.createNotAvailableBooking(newBooking);
        }
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
        room.setTotalRoom(roomRequestDto.getTotalRoom());
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
        response.setTotalRoom(room.getTotalRoom());
        response.setBooking(bookingService.getBookingByRoomId(room.getId()));
        return response;
    }
}