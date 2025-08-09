package com.engly.engly_server.models.events;

import java.time.Instant;

public record TypingEvent(String roomId,
                          String username,
                          boolean isTyping,
                          Instant timestamp) {
}
