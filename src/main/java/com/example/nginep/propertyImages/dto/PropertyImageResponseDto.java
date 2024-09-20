package com.example.nginep.propertyImages.dto;

import com.example.nginep.property.entity.Property;
import lombok.Data;

@Data
public class PropertyImageResponseDto {
    private Long id;
    private String path;
    private String publicKey;
    private Long propertyId;
    private Boolean isThumbnail;
}
