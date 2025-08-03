package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequest;
import com.engly.engly_server.repository.CategoriesRepository;
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

    private final CategoriesRepository categoriesRepository;

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
    public CategoriesDto addCategory(CategoryRequest categoryRequest) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepository.save(Categories.builder()
                        .description(categoryRequest.description())
                        .name(categoryRequest.name())
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
    public CategoriesDto updateCategory(String id, CategoryRequest categoryRequest) {
        return categoriesRepository.findById(id)
                .map(category -> {
                    if (FieldUtil.isValid(categoryRequest.name())) category.setName(categoryRequest.name());
                    if (FieldUtil.isValid(categoryRequest.description())) category.setDescription(categoryRequest.description());

                    return CategoryMapper.INSTANCE.toCategoriesDto(categoriesRepository.save(category));
                })
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ALL_CATEGORIES,
            key = "':all:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 50",
            unless = "#result.content.isEmpty()"
    )
    public Page<CategoriesDto> getAllCategories(Pageable pageable) {
        return categoriesRepository.findAll(pageable).map(CategoryMapper.INSTANCE::toCategoriesDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ID, key = "#categoryId", sync = true)
    public CategoriesDto getCategoryById(String categoryId) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepository.findById(categoryId).orElseThrow(()
                        -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(categoryId)))
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.CATEGORY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_ENTITY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_NAME, allEntries = true)
    })
    public void deleteCategory(String categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_NAME, key = "#name.toString()", sync = true)
    public Categories findByName(CategoryType name) {
        return categoriesRepository.findByName(name).orElseThrow(()
                        -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(name)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ENTITY_ID, key = "#id", sync = true)
    public Categories findCategoryEntityById(String id) {
        return categoriesRepository.findById(id).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }
}
