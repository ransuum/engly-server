package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.request.CategoryRequest;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.mapper.CategoryMapper;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
    public CategoriesDto addCategory(CategoryRequest categoryRequest) {
        if (categoriesRepository.existsByName(categoryRequest.name()))
            throw new EntityAlreadyExistsException("Category with name " + categoryRequest.name() + " already exists");

        var save = categoriesRepository.save(Categories.builder()
                .description(categoryRequest.description())
                .name(categoryRequest.name())
                .build());

        return categoryMapper.toCategoriesDto(save);
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = CacheName.CATEGORY_ENTITY_ID, key = "#id"),
                    @CachePut(value = CacheName.CATEGORY_NAME, key = "#result.name.toString()")
            },
            evict = @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
    )
    public CategoriesDto updateCategory(String id, CategoryRequest categoryRequest) {
        return categoriesRepository.findById(id)
                .map(category -> {
                    if (categoryRequest.name() != null) category.setName(categoryRequest.name());
                    if (StringUtils.isNotBlank(categoryRequest.description()))
                        category.setDescription(categoryRequest.description());

                    return categoryMapper.toCategoriesDto(categoriesRepository.save(category));
                })
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ALL_CATEGORIES,
            key = "':all:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<CategoriesDto> getAllCategories(Pageable pageable) {
        return categoriesRepository.findAll(pageable)
                .map(categories -> categoryMapper
                        .toCategoriesDto(categories, categoriesRepository.roomsCount(categories.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriesDto getCategoryById(String categoryId) {
        var categories = categoriesRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(categoryId)));
        return categoryMapper.toCategoriesDto(categories, categoriesRepository.roomsCount(categories.getId()));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.CATEGORY_ENTITY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_NAME, allEntries = true),
            @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
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
    @Cacheable(value = CacheName.CATEGORY_ID_BY_NAME, key = "#name.toString()", sync = true)
    public String getCategoryIdByName(CategoryType name) {
        return categoriesRepository.getCategoryIdByName(name)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(name)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ENTITY_ID, key = "#id", sync = true)
    public Categories findCategoryEntityById(String id) {
        return categoriesRepository.findById(id).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }
}
