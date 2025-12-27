package com.engly.engly_server.service.common;

import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.repository.MessageRepository;
import com.engly.engly_server.service.helper.MessageReadHelper;
import com.engly.engly_server.service.mapper.UserMapper;
import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.response.UserWhoReadsMessageDto;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.repository.MessageReadRepository;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import module java.base;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadService {

    private final MessageReadRepository messageReadRepository;
    private final MessageReadHelper messageReadHelper;
    private final CacheManager cacheManager;
    private final UserService userService;
    private final UserMapper userMapper;
    private final MessageRepository messageRepository;

    @Async
    @Transactional
    public CompletableFuture<Void> markMessageAsRead(MessageRequest messageRequest, String userId) {
        if (CollectionUtils.isEmpty(messageRequest.messageIds())) {
            return CompletableFuture.completedFuture(null);
        }

        var unreadMessageIds = messageRequest.messageIds().stream()
                .filter(messageId -> !messageReadRepository.existsByMessageIdAndUserId(messageId, userId))
                .toList();
        log.info("User {} has {} unread messages to mark as read", userId, unreadMessageIds.size());

        if (unreadMessageIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        var user = userService.findEntityById(userId);
        var messageMap = messageRepository.findAllById(unreadMessageIds)
                .stream().collect(Collectors.toMap(Message::getId, Function.identity()));

        var newReads = unreadMessageIds.stream()
                .map(messageId -> {
                    var message = messageMap.get(messageId);
                    return MessageRead.builder()
                            .messageId(messageId)
                            .roomId(message.getRoomId())
                            .user(user)
                            .build();
                })
                .toList();
        log.info("Created {} new MessageRead entities for user {}", newReads.size(), userId);

        return Objects.requireNonNull(messageReadHelper.batchSaveMessageReads(newReads))
                .thenRun(() -> evictReadStatusCache(messageRequest.messageIds(), userId));
    }

    private void evictReadStatusCache(List<String> messageIds, String userId) {
        Cache messageReadStatusCache = cacheManager.getCache(CacheName.MESSAGE_READ_STATUS);
        if (messageReadStatusCache != null) {
            messageIds.forEach(messageId -> messageReadStatusCache.evict(messageId + "_" + userId));
        }
    }

    @Transactional(readOnly = true)
    public Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable) {
        return messageReadRepository.findAllByMessageId(messageId, pageable)
                .map(mr -> userMapper.toWhoReadMessage(mr.getUser()));
    }
}