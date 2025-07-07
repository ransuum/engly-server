package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import com.engly.engly_server.repo.CategoriesRepo;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.mapper.CategoryMapper;
import com.engly.engly_server.utils.cache.CacheName;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoriesService {
    private final CategoriesRepo categoriesRepo;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = CacheName.CATEGORY_ID, key = "#result.id"),
                    @CachePut(value = CacheName.CATEGORY_NAME, key = "#result.name.toString()"),
                    @CachePut(value = CacheName.CATEGORY_ENTITY_ID, key = "#result.id")
            },
            evict = {
                    @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
            }
    )
    public CategoriesDto addCategory(CategoryRequestDto categoryRequestDto) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepo.save(Categories.builder()
                        .description(categoryRequestDto.description())
                        .name(categoryRequestDto.name())
                        .build())
        );
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = CacheName.CATEGORY_ID, key = "#id"),
                    @CachePut(value = CacheName.CATEGORY_NAME, key = "#result.name.toString()")
            },
            evict = {
                    @CacheEvict(value = CacheName.CATEGORY_ENTITY_ID, key = "#id")
            }
    )
    public CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto) {
        return categoriesRepo.findById(id)
                .map(category -> {
                    if (FieldUtil.isValid(categoryRequestDto.name())) category.setName(categoryRequestDto.name());
                    if (FieldUtil.isValid(categoryRequestDto.description())) category.setDescription(categoryRequestDto.description());

                    return CategoryMapper.INSTANCE.toCategoriesDto(categoriesRepo.save(category));
                })
                .orElseThrow(() -> new NotFoundException("Category not found while updating"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ALL_CATEGORIES,
            key = "':native:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<CategoriesDto> getAllCategories(Pageable pageable) {
        return categoriesRepo.findAllNative(pageable).map(CategoryMapper.INSTANCE::toCategoriesDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ID, key = "#categoryId", sync = true)
    public CategoriesDto getCategoryById(String categoryId) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepo.findById(categoryId).orElseThrow(()
                        -> new NotFoundException("Category not found by id: " + categoryId))
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.CATEGORY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_ENTITY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_NAME, allEntries = true)
    })
    public void deleteCategory(String categoryId) {
        categoriesRepo.delete(categoriesRepo.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Category not found while deleting")));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_NAME, key = "#name.toString()", sync = true)
    public Categories findByName(CategoryType name) {
        return categoriesRepo.findByName(name).orElseThrow(()
                        -> new NotFoundException("Category not found while finding by category type name"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ENTITY_ID, key = "#id", sync = true)
    public Categories findCategoryEntityById(String id) {
        return categoriesRepo.findById(id).orElseThrow(()
                -> new NotFoundException("Category not found while finding by id: " + id));
    }
}
