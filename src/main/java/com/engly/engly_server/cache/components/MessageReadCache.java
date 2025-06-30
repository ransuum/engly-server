package com.engly.engly_server.cache.components;

public interface MessageReadCache {
    boolean hasUserReadMessage(String messageId, String userId);

    boolean updateReadStatus(String messageId, String userId, boolean status);
}
