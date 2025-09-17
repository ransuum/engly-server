package com.engly.engly_server.models.dto.response;

import java.time.Instant;

public record MessagesDto(
        String id,
        String content,
        Instant createdAt,
        RoomDtoShort room,
        boolean isEdited,
        boolean isDeleted,
        Instant updatedAt,
        MessageWriter user) { }
