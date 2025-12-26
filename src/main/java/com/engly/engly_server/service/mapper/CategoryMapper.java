package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "activeRoomsCount", source = "count")
    @Mapping(target = "icon", expression = "java(categories.getName().getIcon())")
    @Mapping(target = "name", expression = "java(categories.getName().getVal())")
    CategoriesDto toCategoriesDto(Categories categories, int count);

    @Mapping(target = "activeRoomsCount", ignore = true)
    CategoriesDto toCategoriesDto(Categories categories);
}
