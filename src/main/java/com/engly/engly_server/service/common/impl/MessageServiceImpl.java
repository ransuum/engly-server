package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.listeners.models.MessagesViewedEvent;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.*;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final RoomService roomService;
    private final UserService userService;
    private final SecurityService service;
    private final ApplicationEventPublisher publisher;
    private final ChatParticipantsService chatParticipantsService;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = CacheName.MESSAGE_ID, key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#messageRequestDto.roomId()"),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_NATIVE, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_CURSOR, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGE_COUNT_BY_ROOM, key = "#messageRequestDto.roomId()"),
                    @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, allEntries = true)
            }
    )
    public MessagesDto sendMessage(MessageRequestDto messageRequestDto) {
        final var user = userService.findUserEntityByEmail(service.getCurrentUserEmail());
        final var room = roomService.findRoomEntityById(messageRequestDto.roomId());

        chatParticipantsService.addParticipant(room, user, Roles.ROLE_USER);

        final var savedMessage = messageRepo.save(Message.builder()
                .isEdited(Boolean.FALSE)
                .isDeleted(Boolean.FALSE)
                .content(messageRequestDto.content())
                .user(user)
                .room(room)
                .build());
        return MessageMapper.INSTANCE.toMessageDto(savedMessage);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_ID, key = "#id"),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, allEntries = true)
            }
    )
    public MessagesDto deleteMessage(String id) {
        return messageRepo.findById(id).map(message -> {
                    messageRepo.delete(message);
                    return MessageMapper.INSTANCE.toMessageDto(message);
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.MESSAGE_ID, key = "#id")
    public MessagesDto findById(String id) {
        return messageRepo.findById(id)
                .map(MessageMapper.INSTANCE::toMessageDto)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }

    @Override
    @Caching(
            put = {@CachePut(value = CacheName.MESSAGE_ID, key = "#id")},
            evict = {@CacheEvict(value = CacheName.MESSAGES_BY_ROOM, allEntries = true)}
    )
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
                    message.setIsEdited(Boolean.TRUE);
                    return MessageMapper.INSTANCE.toMessageDto(messageRepo.save(message));
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.MESSAGES_BY_ROOM_CURSOR,
            key = "#roomId + ':cursor:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.findAllMessagesByRoomIdContainingKeyString(roomId, pageable, keyString)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.MESSAGES_BY_ROOM_NATIVE,
            key = "#roomId + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable) {
        final var messages = messageRepo.findMessagesByRoomIdPaginated(roomId, pageable);
        final var id = userService.getUserIdByEmail(service.getCurrentUserEmail());

        publisher.publishEvent(new MessagesViewedEvent(messages.getContent(), id));

        return messages.map(MessageMapper.INSTANCE::toMessageDto);
    }
}
