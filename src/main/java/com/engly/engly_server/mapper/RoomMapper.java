package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CategoryMapper.class,
        UserMapper.class,
        ChatParticipantMapper.class,
        ModerationMapper.class,
        MessageMapper.class })
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(
            target = "lastMessage",
            expression = "java(MessageMapper.INSTANCE.getLastMessage(rooms))"
    )
    @Mapping(
            target = "members",
            expression = "java(rooms.getChatParticipants() != null ? (long) rooms.getChatParticipants().size() : 0L)"
    )
    RoomsDto roomToDto(Rooms rooms);
}
