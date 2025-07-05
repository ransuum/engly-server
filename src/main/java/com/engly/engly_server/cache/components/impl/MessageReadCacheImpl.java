package com.engly.engly_server.cache.components.impl;

import com.engly.engly_server.cache.components.MessageReadCache;
import com.engly.engly_server.repo.MessageReadRepo;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

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
}
