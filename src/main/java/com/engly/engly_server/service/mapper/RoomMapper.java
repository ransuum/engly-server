package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.RoomDtoShort;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.service.common.ChatParticipantsService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.mapstruct.*;

@Mapper(uses = {
        CategoryMapper.class,
        UserMapper.class,
        ChatParticipantMapper.class,
        ModerationMapper.class,
        MessageMapper.class},
        componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "members", expression = "java(getMemberCount(rooms.getId(), chatParticipantsService))")
    @NonNull RoomsDto roomToDto(@NonNull Rooms rooms, @NonNull @Context ChatParticipantsService chatParticipantsService);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    @NonNull RoomsDto roomToDto(@NonNull Rooms rooms);

    @NonNull RoomDtoShort roomToDtoShort(@NonNull Rooms rooms);

    default int getMemberCount(@Nullable String roomId, @NonNull ChatParticipantsService chatParticipantsService) {
        if (roomId == null) return 0;
        return chatParticipantsService.countActiveParticipants(roomId);
    }
}
