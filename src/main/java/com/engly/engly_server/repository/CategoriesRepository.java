package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, String> {
    boolean existsByName(CategoryType name);

    Optional<Categories> findByName(CategoryType name);

    @Query("SELECT count(*) from Rooms r where r.categoryId = :categoryId")
    int roomsCount(String categoryId);

    @Query("SELECT c.id from Categories c where c.name = :name")
    Optional<String> getCategoryIdByName(CategoryType name);
}
