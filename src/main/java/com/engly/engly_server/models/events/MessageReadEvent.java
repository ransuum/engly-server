package com.engly.engly_server.models.events;

import java.time.Instant;
import java.util.List;

public record MessageReadEvent(List<String> messageId, String roomId, Instant timestamp) { }
