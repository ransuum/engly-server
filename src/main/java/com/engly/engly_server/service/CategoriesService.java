package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriesService {
    CategoriesDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto);

    Page<CategoriesDto> getAllCategories(Pageable pageable);

    CategoriesDto getCategoryById(String categoryId);

    void deleteCategory(String categoryId);

    CategoriesDto findByName(CategoryType name);
}
