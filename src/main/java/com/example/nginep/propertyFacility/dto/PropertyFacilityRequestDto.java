package com.example.nginep.propertyFacility.dto;

import com.example.nginep.property.entity.Property;
import com.example.nginep.propertyFacility.entity.PropertyFacility;
import lombok.Data;

@Data
public class PropertyFacilityRequestDto {
    private String value;
    private Long propertyId;

    public PropertyFacility toEntity(Property property) {
        PropertyFacility newPropertyFacility = new PropertyFacility();
        newPropertyFacility.setValue(value);
        newPropertyFacility.setProperty(property);
        return newPropertyFacility;
    }
}
