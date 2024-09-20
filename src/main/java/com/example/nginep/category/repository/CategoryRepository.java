package com.example.nginep.category.repository;

import com.example.nginep.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long tenantId);
}
