package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.MessagesDto;
import com.engly.engly_server.models.dto.response.RoomDtoShort;
import com.engly.engly_server.models.entity.Message;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserMapper.class, CategoryMapper.class},
        componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", source = "message.id")
    @NonNull MessagesDto toMessageDto(@NonNull Message message, @NonNull RoomDtoShort room);

}
