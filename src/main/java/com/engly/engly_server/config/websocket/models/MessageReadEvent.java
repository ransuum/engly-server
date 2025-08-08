package com.engly.engly_server.config.websocket.models;

import java.time.Instant;
import java.util.List;

public record MessageReadEvent(List<String> messageId, String roomId, Instant timestamp) { }
