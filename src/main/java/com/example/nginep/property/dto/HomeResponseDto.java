package com.example.nginep.property.dto;

import com.example.nginep.category.dto.CategoryResponseDto;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class HomeResponseDto {
    private List<PropertyCitiesResponseDto> Cities;

    private List<CategoryResponseDto> categories;

    private Page<SearchResponseDto> properties;
}
