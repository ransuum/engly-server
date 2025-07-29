package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.googleDrive.GoogleDriveService;
import com.engly.engly_server.listeners.models.MessagesViewedEvent;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.create.CreateMessageData;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
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
    private final GoogleDriveService driveService;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = CacheName.MESSAGE_ID, key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_COUNT_BY_ROOM, key = "#createMessageData.roomId()"),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_NATIVE, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_CURSOR, allEntries = true),
                    @CacheEvict(value = CacheName.ROOM_DTO_ID, key = "#createMessageData.roomId()"),
                    @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#createMessageData.roomId()"),
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
                    @CacheEvict(value = CacheName.ROOM_BY_CATEGORY_AND_KEY, allEntries = true)
            }
    )
    public MessagesDto sendMessage(CreateMessageData createMessageData) {
        final var user = userService.findUserEntityByEmail(service.getCurrentUserEmail());
        final var room = roomService.findRoomEntityById(createMessageData.roomId());

        chatParticipantsService.addParticipant(room, user, RoomRoles.USER);

        final var savedMessage = messageRepo.save(Message.builder()
                .isEdited(Boolean.FALSE)
                .isDeleted(Boolean.FALSE)
                .content(createMessageData.content())
                .imageUrl(driveService.getImageWebViewLink(createMessageData.imageId()))
                .user(user)
                .room(room)
                .build());
        return MessageMapper.INSTANCE.toMessageDto(savedMessage);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_ID, key = "#id"),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_NATIVE, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_CURSOR, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGE_COUNT_BY_ROOM, allEntries = true),
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
                    @CacheEvict(value = CacheName.ROOM_BY_CATEGORY_AND_KEY, allEntries = true)
            }
    )
    public void deleteMessage(String id) {
        messageRepo.findById(id).ifPresentOrElse(_ -> messageRepo.deleteById(id),
                () -> {
                    throw new NotFoundException(NOT_FOUND_MESSAGE);
                });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.MESSAGE_ID, key = "#id")
    public MessagesDto findById(String id) {
        return messageRepo.findById(id)
                .map(MessageMapper.INSTANCE::toMessageDto)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    @Override
    @Caching(
            put = {@CachePut(value = CacheName.MESSAGE_ID, key = "#id")},
            evict = {
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_NATIVE, allEntries = true),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM_CURSOR, allEntries = true)
            }
    )
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
                    message.setIsEdited(Boolean.TRUE);
                    return MessageMapper.INSTANCE.toMessageDto(messageRepo.save(message));
                })
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.MESSAGES_BY_ROOM_CURSOR,
            key = "#roomId + ':cursor:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.search(roomId, keyString, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    @Transactional
    @Cacheable(
            value = CacheName.MESSAGES_BY_ROOM_NATIVE,
            key = "#roomId + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 5 && #pageable.pageSize <= 50",
            unless = "#result.content.isEmpty()"
    )
    public Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable) {
        final var messages = messageRepo.findActive(roomId, pageable);
        final var id = userService.getUserIdByEmail(service.getCurrentUserEmail());

        publisher.publishEvent(new MessagesViewedEvent(messages.getContent(), id));

        return messages.map(MessageMapper.INSTANCE::toMessageDto);
    }
}
