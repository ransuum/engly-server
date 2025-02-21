package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.entity.Moderation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModerationMapper {
    ModerationMapper INSTANCE = Mappers.getMapper(ModerationMapper.class);

    ModerationDto toDto(Moderation model);
}
