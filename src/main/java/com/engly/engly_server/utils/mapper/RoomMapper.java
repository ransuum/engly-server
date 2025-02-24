package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    RoomsDto roomToDto(Rooms rooms);
}
