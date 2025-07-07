package com.engly.engly_server.models.dto;

import java.time.Instant;

public record MessagesDto(String id,
                          String roomId,
                          MessageWriter user,
                          String content,
                          Instant createdAt,
                          Instant updatedAt,
                          Boolean isEdited,
                          Boolean isDeleted) {
}
