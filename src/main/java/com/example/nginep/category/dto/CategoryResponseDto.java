package com.example.nginep.category.dto;

import lombok.Data;

@Data
public class CategoryResponseDto {
    private Long id;
    private String value;
    private String label;
    private Long tenantId;
}
