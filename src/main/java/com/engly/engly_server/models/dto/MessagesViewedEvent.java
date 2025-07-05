package com.engly.engly_server.models.dto;

import java.util.List;

public record MessagesViewedEvent(List<String> messageIds, String userId) { }
