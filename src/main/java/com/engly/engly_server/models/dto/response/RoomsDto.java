package com.engly.engly_server.models.dto.response;

import lombok.Builder;

@Builder
public record RoomsDto(String id,
                       String name,
                       String description,
                       MessagesDto lastMessage,
                       int members) {
}
