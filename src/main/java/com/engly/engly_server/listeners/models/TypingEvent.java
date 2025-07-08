package com.engly.engly_server.listeners.models;

import java.time.Instant;

public record TypingEvent(String roomId,
                          String username,
                          boolean isTyping,
                          Instant timestamp) {
}
