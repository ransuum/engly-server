package com.engly.engly_server.models.dto;

import java.time.Instant;
import java.util.List;

public record RoomsDto(String id,
                       String name,
                       String description,
                       Instant createdAt,
                       Instant updatedAt,
                       UsersDto creator,
                       MessagesDto lastMessage,
                       Long members,
                       List<ChatParticipantsDto> chatParticipants,
                       List<ModerationDto> moderation,
                       List<StatisticsDto> statistics) {
}
