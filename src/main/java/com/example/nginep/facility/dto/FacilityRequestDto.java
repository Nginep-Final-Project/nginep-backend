package com.example.nginep.facility.dto;

import com.example.nginep.facility.entity.Facility;
import com.example.nginep.users.entity.Users;
import lombok.Data;

@Data
public class FacilityRequestDto {
    public Long id;
    private String value;

    public Facility toEntity(Users user) {
        Facility newFacility = new Facility();
        newFacility.setValue(value.trim().toLowerCase().replace(" ", "-"));
        newFacility.setLabel(value);
        newFacility.setUser(user);
        return newFacility;
    }
}
