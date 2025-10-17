package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.request.CategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriesService {

    CategoriesDto addCategory(CategoryRequest categoryRequest);

    CategoriesDto updateCategory(String id, CategoryRequest categoryRequest);

    Page<CategoriesDto> getAllCategories(Pageable pageable);

    CategoriesDto getCategoryById(String categoryId);

    void deleteCategory(String categoryId);

    Categories findByName(CategoryType name);

    String getCategoryIdByName(CategoryType name);

    Categories findCategoryEntityById(String id);
}
