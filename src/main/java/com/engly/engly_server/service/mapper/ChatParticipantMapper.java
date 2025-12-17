package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.ChatParticipantsDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, RoomMapper.class, CategoryMapper.class},
        componentModel = "spring")
public interface ChatParticipantMapper {

    @Mapping(
            target = "username",
            expression = "java(chatParticipants.getUser().getUsername())"
    )
    @Mapping(
            target = "userId",
            expression = "java(chatParticipants.getUser().getId())"
    )
    @NonNull ChatParticipantsDto toDtoForRooms(@NonNull ChatParticipants chatParticipants);
}
