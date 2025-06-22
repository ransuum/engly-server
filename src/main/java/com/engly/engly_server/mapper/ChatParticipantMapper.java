package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, RoomMapper.class, CategoryMapper.class})
public interface ChatParticipantMapper {
    ChatParticipantMapper INSTANCE = Mappers.getMapper(ChatParticipantMapper.class);

    @Mapping(
            target = "username",
            expression = "java(chatParticipants.getUser().getUsername())"
    )
    @Mapping(
            target = "userId",
            expression = "java(chatParticipants.getUser().getId())"
    )
    ChatParticipantsDto toDtoForRooms(ChatParticipants chatParticipants);
}
