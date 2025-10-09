package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.cache.CacheCoordinator;
import com.engly.engly_server.cache.components.MessageReadCache;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.response.UserWhoReadsMessageDto;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.repository.MessageReadRepository;
import com.engly.engly_server.service.common.MessageReadService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessageReadServiceImpl implements MessageReadService {

    private final MessageReadRepository messageReadRepository;
    private final MessageReadCache messageReadCache;
    private final UserService userService;

    public MessageReadServiceImpl(MessageReadRepository messageReadRepository,
                                  CacheCoordinator messageReadCache, UserService userService) {
        this.messageReadRepository = messageReadRepository;
        this.messageReadCache = messageReadCache.getMessageReadCache();
        this.userService = userService;
    }

    @Override
    @Async
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USERS_WHO_READ_MESSAGE, allEntries = true),
            @CacheEvict(value = CacheName.MESSAGE_READ_STATUS, allEntries = true)
    })
    public CompletableFuture<Void> markMessageAsRead(MessageRequest messageRequest, String userId) {
        if (messageRequest.messageIds() == null || messageRequest.messageIds().isEmpty()) return null;

        var futures = messageRequest.messageIds().stream()
                .map(messageId -> CompletableFuture.supplyAsync(() ->
                        new AbstractMap.SimpleEntry<>(messageId,
                                messageReadCache.hasUserReadMessage(messageId, userId))))
                .toList();

        var unreadMessageIds = futures.stream()
                .map(CompletableFuture::join)
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toList();

        if (unreadMessageIds.isEmpty()) return null;

        var newReads = unreadMessageIds.stream()
                .map(messageId -> MessageRead.builder()
                        .messageId(messageId)
                        .user(userService.findEntityById(userId))
                        .build())
                .toList();

        return messageReadCache.batchSaveMessageReads(newReads);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.USERS_WHO_READ_MESSAGE,
            key = "#messageId + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 5 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable) {
        return messageReadRepository.findAllByMessageId(messageId, pageable)
                .map(mr -> UserMapper.INSTANCE.toWhoReadMessage(mr.getUser()));
    }
}