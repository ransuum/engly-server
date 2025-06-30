package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.cache.CachingManagement;
import com.engly.engly_server.cache.components.impl.MessageReadCacheImpl;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.UserWhoReadsMessageDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.MessageReadRepo;
import com.engly.engly_server.service.common.MessageReadService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadServiceImpl implements MessageReadService {

    private final MessageReadRepo messageReadRepository;
    private final CachingManagement cachingManagement;

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.USERS_WHO_READ_MESSAGE, allEntries = true)
            }
    )
    public void markMessageAsRead(List<String> messageIds, String userId) {
        final var cache = cachingManagement.getMessageReadCache();
        List<MessageRead> newReads = messageIds.parallelStream()
                .filter(messageId -> !cache.hasUserReadMessage(messageId, userId))
                .map(messageId -> MessageRead.builder()
                        .message(Message.builder().id(messageId).build())
                        .user(Users.builder().id(userId).build())
                        .build())
                .toList();
        if (!newReads.isEmpty()) {
            messageReadRepository.saveAll(newReads);
            newReads.forEach(read -> cache.
                    updateReadStatus(read.getMessage().getId(), read.getUser().getId(), true));
        }
    }

    @Override
    @Cacheable(
            value = CacheName.USERS_WHO_READ_MESSAGE,
            key = "#messageId + '_page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize",
            unless = "#result.isEmpty()"
    )
    public Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable) {
        return messageReadRepository.findAllByMessageId(messageId, pageable)
                .map(mr -> UserMapper.INSTANCE.toWhoReadMessage(mr.getUser()));
    }
}