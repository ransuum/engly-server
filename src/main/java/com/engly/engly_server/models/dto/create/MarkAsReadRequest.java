package com.engly.engly_server.models.dto.create;

import java.util.List;

public record MarkAsReadRequest(List<String> messageId, String roomId) { }
