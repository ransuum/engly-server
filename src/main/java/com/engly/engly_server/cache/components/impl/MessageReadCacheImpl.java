package com.engly.engly_server.cache.components.impl;

import com.engly.engly_server.cache.components.MessageReadCache;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.repo.MessageReadRepo;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageReadCacheImpl implements MessageReadCache {
    private final MessageReadRepo messageReadRepo;

    @Override
    @Cacheable(
            value = CacheName.MESSAGE_READ_STATUS,
            key = "#messageId + '_' + #userId"
    )
    public boolean hasUserReadMessage(String messageId, String userId) {
        return messageReadRepo.existsByMessageIdAndUserId(messageId, userId);
    }

    @Override
    public void batchSaveMessageReads(List<MessageRead> messageReads) {
        if (messageReads == null || messageReads.isEmpty()) {
            log.debug("No message reads to save");
            return;
        }

        log.debug("Batch saving {} message reads", messageReads.size());

        int batchSize = 100;
        for (int i = 0; i < messageReads.size(); i += batchSize) {
            int end = Math.min(i + batchSize, messageReads.size());
            List<MessageRead> batch = messageReads.subList(i, end);

            try {
                messageReadRepo.saveAll(batch);
                log.debug("Saved batch of {} message reads (batch {}/{})",
                        batch.size(), (i / batchSize) + 1, (messageReads.size() + batchSize - 1) / batchSize);
            } catch (Exception e) {
                log.error("Failed to save batch of message reads: {}", e.getMessage(), e);
                throw e;
            }
        }
    }
}
