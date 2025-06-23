package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriesService {
    CategoriesDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto);

    Page<CategoriesDto> getAllCategories(Pageable pageable);

    CategoriesDto getCategoryById(String categoryId);

    void deleteCategory(String categoryId);

    Categories findByName(CategoryType name);

    Categories findCategoryEntityById(String id);
}
