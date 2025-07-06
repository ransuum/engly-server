package com.engly.engly_server.listeners.models;

import java.util.List;

public record MessagesViewedEvent(List<String> messageIds, String userId) { }
