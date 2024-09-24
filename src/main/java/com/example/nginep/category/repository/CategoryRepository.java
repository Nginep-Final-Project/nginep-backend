package com.example.nginep.category.repository;

import com.example.nginep.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long tenantId);

    @Query("SELECT DISTINCT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.value, c.label")
    List<Category> findAllDistinctCategories();
}
