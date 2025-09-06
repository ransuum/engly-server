package com.engly.engly_server.cache.components;

import com.engly.engly_server.models.entity.MessageRead;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageReadCache {
    boolean hasUserReadMessage(String messageId, String userId);

    @Async
    CompletableFuture<Void> batchSaveMessageReads(List<MessageRead> messageReads);
}
