package com.example.nginep.category.service;


import com.example.nginep.category.dto.CategoryRequestDto;
import com.example.nginep.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    List<CategoryResponseDto> getCategoryByTenantId(Long tenantId);
    String deleteCategory(Long categoryId);
}
