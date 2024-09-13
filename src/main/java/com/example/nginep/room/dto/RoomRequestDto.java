package com.example.nginep.room.dto;

import com.example.nginep.property.entity.Property;
import com.example.nginep.room.entity.Room;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer maxOccupancy;
    private Long propertyId;

    public Room toEntity(Property property) {
        Room newRoom = new Room();
        newRoom.setName(name);
        newRoom.setDescription(description);
        newRoom.setPrice(price);
        newRoom.setMaxOccupancy(maxOccupancy);
        newRoom.setProperty(property);
        return newRoom;
    }
}
