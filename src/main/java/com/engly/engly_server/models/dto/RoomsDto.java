package com.engly.engly_server.models.dto;

import java.time.Instant;
import java.util.List;

public record RoomsDto(String id,
                       CategoriesDto category,
                       String name,
                       String description,
                       Instant createdAt,
                       Instant updatedAt,
                       UsersDto creator,
                       List<MessagesDto> messages,
                       List<ChatParticipantsDto> chatParticipants,
                       List<ModerationDto> moderation,
                       List<StatisticsDto> statistics) {
}
