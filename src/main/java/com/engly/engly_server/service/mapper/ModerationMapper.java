package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.ModerationDto;
import com.engly.engly_server.models.entity.Moderation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserMapper.class, RoomMapper.class, CategoryMapper.class},
        componentModel = "spring")
public interface ModerationMapper {
    @Mapping(target = "room", ignore = true)
    ModerationDto toDtoForRoom(Moderation moderation);
}
