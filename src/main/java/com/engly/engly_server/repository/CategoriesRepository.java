package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, String> {
    boolean existsByName(CategoryType name);

    Optional<Categories> findByName(CategoryType name);
}
