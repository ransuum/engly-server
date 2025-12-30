package com.engly.engly_server.service.common;

import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.request.CategoryRequest;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.service.mapper.CategoryMapper;
import com.engly.engly_server.utils.CacheName;
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

import static com.engly.engly_server.exception.handler.ExceptionMessage.CATEGORY_ALREADY_EXISTS;
import static com.engly.engly_server.exception.handler.ExceptionMessage.CATEGORY_NOT_FOUND_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
    public CategoriesDto addCategory(CategoryRequest categoryRequest) {
        if (categoriesRepository.existsByName(categoryRequest.name()))
            throw new EntityAlreadyExistsException(CATEGORY_ALREADY_EXISTS.formatted(categoryRequest.name().name()));

        var category = categoriesRepository.save(Categories.builder()
                .description(categoryRequest.description())
                .name(categoryRequest.name())
                .build());

        return categoryMapper.toCategoriesDto(category);
    }

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
                    category.setName(categoryRequest.name());
                    if (StringUtils.isNotBlank(categoryRequest.description()))
                        category.setDescription(categoryRequest.description());

                    return categoryMapper.toCategoriesDto(categoriesRepository.save(category));
                })
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }

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

    @Transactional(readOnly = true)
    public CategoriesDto getCategoryById(String categoryId) {
        var categories = categoriesRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(categoryId)));
        return categoryMapper.toCategoriesDto(categories, categoriesRepository.roomsCount(categories.getId()));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.CATEGORY_ENTITY_ID, key = "#categoryId"),
            @CacheEvict(value = CacheName.CATEGORY_NAME, allEntries = true),
            @CacheEvict(value = CacheName.ALL_CATEGORIES, allEntries = true)
    })
    public void deleteCategory(String categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_NAME, key = "#name.toString()", sync = true)
    public Categories findByName(CategoryType name) {
        return categoriesRepository.findByName(name).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(name)));
    }

    @Cacheable(value = CacheName.CATEGORY_ID_BY_NAME, key = "#name.toString()", sync = true)
    public String getCategoryIdByName(CategoryType name) {
        return categoriesRepository.getCategoryIdByName(name)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(name)));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.CATEGORY_ENTITY_ID, key = "#id", sync = true)
    public Categories findCategoryEntityById(String id) {
        return categoriesRepository.findById(id).orElseThrow(()
                -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE.formatted(id)));
    }
}
