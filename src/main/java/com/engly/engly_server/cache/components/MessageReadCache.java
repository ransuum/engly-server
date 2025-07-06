package com.engly.engly_server.cache.components;

import com.engly.engly_server.models.entity.MessageRead;

import java.util.List;

public interface MessageReadCache {
    boolean hasUserReadMessage(String messageId, String userId);

    void batchSaveMessageReads(List<MessageRead> messageReads);
}
