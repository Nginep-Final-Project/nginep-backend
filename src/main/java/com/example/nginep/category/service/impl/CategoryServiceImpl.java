package com.example.nginep.category.service.impl;

import com.example.nginep.category.dto.CategoryRequestDto;
import com.example.nginep.category.dto.CategoryResponseDto;
import com.example.nginep.category.entity.Category;
import com.example.nginep.category.repository.CategoryRepository;
import com.example.nginep.category.service.CategoryService;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UsersService usersService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UsersService usersService) {
        this.categoryRepository = categoryRepository;
        this.usersService = usersService;
    }

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Users user = usersService.getDetailUserId(categoryRequestDto.getTenantId());

        Category newCategory = categoryRepository.save(categoryRequestDto.toEntity(user));
        return mapToCategoryResponseDto(newCategory);
    }

    @Override
    public List<CategoryResponseDto> getCategoryByTenantId(Long tenantId) {
        return categoryRepository.findAllByUserId(tenantId).stream().map(this::mapToCategoryResponseDto).toList();
    }

    @Override
    public String deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(()->new NotFoundException("Category with id: " + categoryId + " not found"));
        categoryRepository.deleteById(categoryId);
        return "Category with id: " + categoryId + " has deleted successfully";
    }

    public CategoryResponseDto mapToCategoryResponseDto(Category category) {
        CategoryResponseDto response = new CategoryResponseDto();
        response.setId(category.getId());
        response.setValue(category.getValue());
        response.setLabel(category.getLabel());
        response.setTenantId(category.getUser().getId());
        return response;
    }
}
