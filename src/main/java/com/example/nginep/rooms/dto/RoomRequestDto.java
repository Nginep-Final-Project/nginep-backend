package com.example.nginep.rooms.dto;

import com.example.nginep.property.entity.Property;
import com.example.nginep.rooms.entity.Room;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer maxGuests;
    private Long propertyId;

    public Room toEntity(Property property) {
        Room newRoom = new Room();
        newRoom.setName(name);
        newRoom.setDescription(description);
        newRoom.setBasePrice(basePrice);
        newRoom.setMaxGuests(maxGuests);
        newRoom.setProperty(property);
        return newRoom;
    }
}
