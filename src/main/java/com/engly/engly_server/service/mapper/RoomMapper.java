package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.RoomDtoShort;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import org.mapstruct.*;

@Mapper(uses = {
        CategoryMapper.class,
        UserMapper.class,
        ChatParticipantMapper.class,
        ModerationMapper.class,
        MessageMapper.class},
        componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "members", ignore = true)
    RoomsDto roomToDto(Rooms rooms);

    RoomDtoShort roomToDtoShort(Rooms rooms);
}
