package com.example.nginep.category.controller;

import com.example.nginep.category.dto.CategoryRequestDto;
import com.example.nginep.category.dto.CategoryResponseDto;
import com.example.nginep.category.service.CategoryService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@Validated
@Log
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Response<CategoryResponseDto>> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        return Response.successResponse("Create category success", categoryService.createCategory(categoryRequestDto));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<Response<List<CategoryResponseDto>>> getCategoryByTenantId(@PathVariable Long tenantId) {
        return Response.successResponse("List category by tenant id: " + tenantId, categoryService.getCategoryByTenantId(tenantId));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Response<String>> deleteCategory(@PathVariable Long categoryId) {
        return Response.successResponse("Delete category success", categoryService.deleteCategory(categoryId));
    }
}
