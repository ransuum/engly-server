package com.engly.engly_server.cache.components;

@FunctionalInterface
public interface ChatParticipantCache {
    boolean isParticipantExists(String roomId, String userId);
}
