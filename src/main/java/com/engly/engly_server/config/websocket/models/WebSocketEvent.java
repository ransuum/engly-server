package com.engly.engly_server.config.websocket.models;

import com.engly.engly_server.models.enums.EventType;

public record WebSocketEvent<T>(EventType type, T payload) { }
