package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, CategoryMapper.class})
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "room", ignore = true)
    MessagesDto toMessageDto(Messages message);
}
