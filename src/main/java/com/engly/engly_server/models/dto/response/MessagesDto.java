package com.engly.engly_server.models.dto.response;

import java.time.Instant;

public record MessagesDto(String content, Instant createdAt, String roomId) { }
