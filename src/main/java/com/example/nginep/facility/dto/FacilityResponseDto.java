package com.example.nginep.facility.dto;

import lombok.Data;

@Data
public class FacilityResponseDto {
    private Long id;
    private String value;
    private String label;
    private Long tenantId;
}
