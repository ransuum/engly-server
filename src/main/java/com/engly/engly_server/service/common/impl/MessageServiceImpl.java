package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.googledrive.GoogleDriveService;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.request.CreateMessageData;
import com.engly.engly_server.models.dto.request.MessageSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.MessageRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final AuthenticatedUserProvider service;
    private final ChatParticipantsService chatParticipantsService;
    private final GoogleDriveService driveService;

    @Override
    @Transactional
    public MessagesDto sendMessage(CreateMessageData createMessageData) {
        final var user = userService.findUserEntityByEmail(service.getCurrentUserEmail());
        final var room = roomService.findRoomEntityById(createMessageData.roomId());

        chatParticipantsService.addParticipant(room, user, RoomRoles.USER);

        final var savedMessage = messageRepository.save(Message.builder()
                .isEdited(Boolean.FALSE)
                .isDeleted(Boolean.FALSE)
                .content(createMessageData.content())
                .imageUrl(driveService.getImageThumbnailLink(createMessageData.imageId()))
                .user(user)
                .room(room)
                .build());
        return MessageMapper.INSTANCE.toMessageDto(savedMessage);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_ID, key = "#id"),
                    @CacheEvict(value = CacheName.MESSAGE_COUNT_BY_ROOM, allEntries = true)
            }
    )
    public void deleteMessage(String id) {
        messageRepository.findById(id).ifPresentOrElse(_ -> messageRepository.deleteById(id),
                () -> {
                    throw new NotFoundException(NOT_FOUND_MESSAGE);
                });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.MESSAGE_ID, key = "#id")
    public MessagesDto findById(String id) {
        return messageRepository.findById(id)
                .map(MessageMapper.INSTANCE::toMessageDto)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional
    public MessagesDto editMessage(String id, String content) {
        return messageRepository.findById(id)
                .map(message -> {
                    message.setContent(content);
                    message.setIsEdited(Boolean.TRUE);
                    return MessageMapper.INSTANCE.toMessageDto(messageRepository.save(message));
                })
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable) {
        return messageRepository.findActive(roomId, pageable).map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessagesDto> findMessagesByCriteria(MessageSearchCriteriaRequest request, Pageable pageable) {
        return messageRepository.findAll(request.buildSpecification(), pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }
}
