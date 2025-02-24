package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatParticipantMapper {
    ChatParticipantMapper INSTANCE = Mappers.getMapper(ChatParticipantMapper.class);

    ChatParticipantsDto toChatParticipantsDto(ChatParticipants chatParticipants);
}
