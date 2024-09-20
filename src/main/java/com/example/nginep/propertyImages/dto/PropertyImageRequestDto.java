package com.example.nginep.propertyImages.dto;

import com.example.nginep.property.entity.Property;
import com.example.nginep.propertyImages.entity.PropertyImage;
import lombok.Data;

@Data
public class PropertyImageRequestDto {
    private String path;
    private String publicKey;
    private Long propertyId;
    private Boolean isThumbnail;

    public PropertyImage toEntity(Property property) {
        PropertyImage newPropertyImage = new PropertyImage();
        newPropertyImage.setPath(path);
        newPropertyImage.setPublicKey(publicKey);
        newPropertyImage.setProperty(property);
        newPropertyImage.setIsThumbnail(isThumbnail);
        return newPropertyImage;
    }
}
