package com.engly.engly_server.models.dto.response;

import java.time.Instant;

public record MessagesDto(String id,
                          String roomId,
                          MessageWriter user,
                          String content,
                          String imageUrl,
                          Instant createdAt,
                          Instant updatedAt,
                          Boolean isEdited,
                          Boolean isDeleted) {
}
