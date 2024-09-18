package com.example.nginep.category.dto;

import com.example.nginep.category.entity.Category;
import com.example.nginep.users.entity.Users;
import lombok.Data;

@Data
public class CategoryRequestDto {
    private Long id;
    private String value;


    public Category toEntity(Users user){
        Category newCategory = new Category();
        newCategory.setValue(value.trim().toLowerCase().replace(" ", "-"));
        newCategory.setLabel(value);
        newCategory.setUser(user);
        return newCategory;
    }
}
