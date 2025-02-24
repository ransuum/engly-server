package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessagesDto toMessageDto(Messages message);
}
