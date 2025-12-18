package com.engly.engly_server.service.helper;

import com.engly.engly_server.exception.RepositoryException;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.repository.MessageReadRepository;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class MessageReadHelper {

    private final MessageReadRepository messageReadRepository;
    private final CacheManager cacheManager;

    @Cacheable(
            value = CacheName.MESSAGE_READ_STATUS,
            key = "#messageId + '_' + #userId"
    )
    public boolean hasUserReadMessage(String messageId, String userId) {
        return messageReadRepository.existsByMessageIdAndUserId(messageId, userId);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> batchSaveMessageReads(@Nullable List<MessageRead> messageReads) {
        if (CollectionUtils.isEmpty(messageReads)) {
            log.info("No message reads to save");
            return CompletableFuture.completedFuture(null);
        }

        log.info("Batch saving {} message reads", messageReads.size());

        final List<CompletableFuture<List<MessageRead>>> futures = IntStream.range(0, messageReads.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 100))
                .values()
                .stream()
                .map(indices -> CompletableFuture.supplyAsync(() -> {
                    List<MessageRead> batch = indices.stream()
                            .map(messageReads::get)
                            .toList();

                    try {
                        var saved = messageReadRepository.saveAll(batch);
                        log.info("Saved batch of {} message reads", batch.size());
                        return saved;
                    } catch (Exception e) {
                        throw new RepositoryException("Batch save failed: " +  e.getMessage());
                    }
                }))
                .toList();

        final List<MessageRead> savedReads = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        return CompletableFuture.runAsync(() -> updateCacheForSavedReads(savedReads));
    }


    private void updateCacheForSavedReads(List<MessageRead> savedReads) {
        final var cache = cacheManager.getCache(CacheName.MESSAGE_READ_STATUS);
        if (cache != null) savedReads.forEach(mr ->
                cache.put(mr.getMessageId() + "_" + mr.getUser().getId(), true));
    }
}
