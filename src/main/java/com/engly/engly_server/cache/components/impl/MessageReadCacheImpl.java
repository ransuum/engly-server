package com.engly.engly_server.cache.components.impl;

import com.engly.engly_server.cache.components.MessageReadCache;
import com.engly.engly_server.exception.RepositoryException;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.repository.MessageReadRepository;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageReadCacheImpl implements MessageReadCache {
    private final MessageReadRepository messageReadRepository;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(
            value = CacheName.MESSAGE_READ_STATUS,
            key = "#messageId + '_' + #userId"
    )
    public boolean hasUserReadMessage(String messageId, String userId) {
        return messageReadRepository.existsByMessageIdAndUserId(messageId, userId);
    }

    @Override
    @Transactional
    public void batchSaveMessageReads(List<MessageRead> messageReads) {
        if (messageReads == null || messageReads.isEmpty()) {
            log.debug("No message reads to save");
            return;
        }

        log.debug("Batch saving {} message reads", messageReads.size());

        final List<CompletableFuture<List<MessageRead>>> futures = IntStream.range(0, messageReads.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 100))
                .values()
                .stream()
                .map(indices -> CompletableFuture.supplyAsync(() -> {
                    final var batch = indices.stream()
                            .map(messageReads::get)
                            .toList();

                    try {
                        var saved = messageReadRepository.saveAll(batch);
                        log.debug("Saved batch of {} message reads", batch.size());
                        return saved;
                    } catch (Exception e) {
                        log.error("Failed to save batch: {}", e.getMessage());
                        throw new RepositoryException("Batch save failed", e);
                    }
                }))
                .toList();

        final var savedReads = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        updateCacheForSavedReads(savedReads);
    }

    private void updateCacheForSavedReads(List<MessageRead> savedReads) {
        final var cache = cacheManager.getCache(CacheName.MESSAGE_READ_STATUS);
        if (cache != null) savedReads.forEach(mr ->
                cache.put(mr.getMessageId() + "_" + mr.getUserId(), true));
    }
}
