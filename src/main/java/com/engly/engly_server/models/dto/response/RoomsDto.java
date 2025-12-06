package com.engly.engly_server.models.dto.response;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record RoomsDto(String id,
                       String name,
                       @Nullable String description,
                       MessagesDto lastMessage,
                       int members) {
}
