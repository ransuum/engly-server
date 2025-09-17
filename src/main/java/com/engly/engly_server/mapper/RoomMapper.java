package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.response.RoomDtoShort;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.service.common.ChatParticipantsService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CategoryMapper.class,
        UserMapper.class,
        ChatParticipantMapper.class,
        ModerationMapper.class,
        MessageMapper.class},
        componentModel = "spring")
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(target = "members", expression = "java(getMemberCount(rooms.getId(), chatParticipantsService))")
    RoomsDto roomToDto(Rooms rooms, @Context ChatParticipantsService chatParticipantsService);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    RoomsDto roomToDto(Rooms rooms);

    RoomDtoShort roomToDtoShort(Rooms rooms);

    default int getMemberCount(String roomId, ChatParticipantsService chatParticipantsService) {
        if (roomId == null) return 0;
        return chatParticipantsService.countActiveParticipants(roomId);
    }
}
