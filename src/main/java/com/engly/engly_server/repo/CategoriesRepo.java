package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoriesRepo extends JpaRepository<Categories, String> {
    Optional<Categories> findByName(CategoryType name);

    @Query(value = " SELECT * FROM categories", nativeQuery = true)
    Page<Categories> findAllNative(Pageable pageable);
}
