package com.engly.engly_server.cache.components;

@FunctionalInterface
public interface MessageReadCache {
    boolean hasUserReadMessage(String messageId, String userId);
}
