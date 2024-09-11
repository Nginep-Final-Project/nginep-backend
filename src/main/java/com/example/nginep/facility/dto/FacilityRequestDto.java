package com.example.nginep.facility.dto;

import com.example.nginep.facility.entity.Facility;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.Data;

@Data
public class FacilityRequestDto {
    private String value;
    private Long tenantId;

    public Facility toEntity(Users user){
        Facility newFacility = new Facility();
        newFacility.setValue(value.trim().toLowerCase().replace(" ", "-"));
        newFacility.setLabel(value);
        newFacility.setUser(user);
        return newFacility;
    }
}
