package com.engly.engly_server.cache.components;

public interface ChatParticipantCache {
    boolean isParticipantExists(String roomId, String userId);
}
