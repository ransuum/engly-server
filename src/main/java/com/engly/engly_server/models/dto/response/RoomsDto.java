package com.engly.engly_server.models.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RoomsDto(String id,
                       String name,
                       String description,
                       MessagesDto lastMessage,
                       int members,
                       List<ChatParticipantsDto> chatParticipants) {
}
