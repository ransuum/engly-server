package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "activeRoomsCount", source = "count")
    @Mapping(target = "icon", expression = "java(categories.getName().getIcon())")
    @Mapping(target = "name", expression = "java(categories.getName().getVal())")
    @NonNull CategoriesDto toCategoriesDto(@NonNull Categories categories, int count);

    @Mapping(target = "activeRoomsCount", ignore = true)
    @NonNull CategoriesDto toCategoriesDto(@NonNull Categories categories);
}
