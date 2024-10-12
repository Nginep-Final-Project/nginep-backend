package com.example.nginep.category.service;


import com.example.nginep.category.dto.CategoryRequestDto;
import com.example.nginep.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto editCategory(CategoryRequestDto categoryRequestDto);
    List<CategoryResponseDto> getCategoryByTenantId();
    List<CategoryResponseDto> getAllCategory();
    String deleteCategory(Long categoryId);
}
