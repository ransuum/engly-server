package com.engly.engly_server.models.dto.response;

import java.time.Instant;

public record MessagesDto(String id, String content, Instant createdAt, String roomId) { }
