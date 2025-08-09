package com.engly.engly_server.models.dto.request;

import java.util.List;

public record MarkAsReadRequest(String roomId, List<String> messageId) { }
