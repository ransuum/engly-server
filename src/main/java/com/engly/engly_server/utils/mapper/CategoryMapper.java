package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class CategoryMapper {
    public static CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "activeRoomsCount", source = "activeRoomsCount")
    protected abstract CategoriesDto toCategoriesDto(Categories categories, Integer activeRoomsCount);


    public CategoriesDto toCategoriesDto(Categories categories) {
        return toCategoriesDto(categories, categories.getRooms().size());
    }
}
