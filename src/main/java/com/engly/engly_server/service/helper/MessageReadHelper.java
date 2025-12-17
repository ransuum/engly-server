package com.engly.engly_server.service.helper;

import com.engly.engly_server.models.entity.MessageRead;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@NullMarked
public interface MessageReadHelper {
    boolean hasUserReadMessage(String messageId, String userId);

    @Async
    @Nullable CompletableFuture<Void> batchSaveMessageReads(@Nullable List<MessageRead> messageReads);
}
