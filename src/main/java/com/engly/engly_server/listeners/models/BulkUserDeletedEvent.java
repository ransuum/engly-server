package com.engly.engly_server.listeners.models;

import java.util.List;

public record BulkUserDeletedEvent(List<String> userIds) { }
