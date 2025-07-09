package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.cache.CachingManagement;
import com.engly.engly_server.cache.components.MessageReadCache;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.UserWhoReadsMessageDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.MessageReadRepo;
import com.engly.engly_server.service.common.MessageReadService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MessageReadServiceImpl implements MessageReadService {

    private final MessageReadRepo messageReadRepository;
    private final MessageReadCache messageReadCache;

    public MessageReadServiceImpl(MessageReadRepo messageReadRepository, CachingManagement messageReadCache) {
        this.messageReadRepository = messageReadRepository;
        this.messageReadCache = messageReadCache.getMessageReadCache();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.USERS_WHO_READ_MESSAGE, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGE_READ_STATUS, allEntries = true)
            }
    )
    public void markMessageAsRead(List<String> messageIds, String userId) {
        if (messageIds == null || messageIds.isEmpty()) return;

        final var unreadMessageIds = messageIds.parallelStream()
                .filter(messageId -> !messageReadCache.hasUserReadMessage(messageId, userId))
                .toList();
        if (unreadMessageIds.isEmpty()) return;

        final var newReads = unreadMessageIds.stream()
                .map(messageId -> MessageRead.builder()
                        .messageId(messageId)
                        .userId(userId)
                        .message(Message.builder().id(messageId).build())
                        .user(Users.builder().id(userId).build())
                        .build())
                .toList();

        messageReadCache.batchSaveMessageReads(newReads);
    }

    @Override
    @Cacheable(
            value = CacheName.USERS_WHO_READ_MESSAGE,
            key = "#messageId + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable) {
        return messageReadRepository.findAllByMessageId(messageId, pageable)
                .map(mr -> UserMapper.INSTANCE.toWhoReadMessage(mr.getUser()));
    }
}