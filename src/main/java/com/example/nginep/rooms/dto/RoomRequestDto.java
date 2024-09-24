package com.example.nginep.rooms.dto;

import com.example.nginep.bookings.dto.CreateNotAvailableBookingDTO;
import com.example.nginep.property.entity.Property;
import com.example.nginep.rooms.entity.Room;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomRequestDto {
    private Long id;
    private String name;
    private String roomPicture;
    private String roomPictureId;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private Integer totalRoom;
    private List<CreateNotAvailableBookingDTO> notAvailableDates;
    private Long propertyId;

    public Room toEntity(Property property) {
        Room newRoom = new Room();
        newRoom.setName(name);
        newRoom.setRoomPicture(roomPicture);
        newRoom.setRoomPictureId(roomPictureId);
        newRoom.setDescription(description);
        newRoom.setMaxGuests(maxGuests);
        newRoom.setBasePrice(basePrice);
        newRoom.setTotalRoom(totalRoom);
        newRoom.setProperty(property);
        return newRoom;
    }
}
