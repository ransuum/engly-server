package com.engly.engly_server.models.dto.response;

import java.util.List;

public record RoomsDto(String id,
                       String name,
                       String description,
                       MessagesDto lastMessage,
                       Long members,
                       List<ChatParticipantsDto> chatParticipants) {
}
