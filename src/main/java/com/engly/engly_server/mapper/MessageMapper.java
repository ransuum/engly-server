package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.Rooms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;

@Mapper(uses = {UserMapper.class, CategoryMapper.class})
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(source = "message.room.id", target = "roomId")
    MessagesDto toMessageDto(Message message);

    @Named("getLastMessage")
    default MessagesDto getLastMessage(Rooms room) {
        if (room.getMessages() == null || room.getMessages().isEmpty()) return null;
        return room.getMessages()
                .stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(this::toMessageDto)
                .orElse(null);
    }
}
