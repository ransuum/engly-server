package com.engly.engly_server.models.dto;

import java.util.List;

public record RoomsDto(String id,
                       String name,
                       String description,
                       UserCreator creator,
                       MessagesDto lastMessage,
                       Long members,
                       List<ChatParticipantsDto> chatParticipants) {
}
