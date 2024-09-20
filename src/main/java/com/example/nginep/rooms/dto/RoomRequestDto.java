package com.example.nginep.rooms.dto;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.property.entity.Property;
import com.example.nginep.rooms.entity.Room;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private Integer totalRoom;
    private CreateBookingDTO Booking;
    private Long propertyId;

    public Room toEntity(Property property) {
        Room newRoom = new Room();
        newRoom.setName(name);
        newRoom.setDescription(description);
        newRoom.setMaxGuests(maxGuests);
        newRoom.setBasePrice(basePrice);
        newRoom.setTotalRoom(totalRoom);
        newRoom.setProperty(property);
        return newRoom;
    }
}
