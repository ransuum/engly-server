package com.engly.engly_server.service.common;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.googledrive.GoogleDriveService;
import com.engly.engly_server.service.mapper.MessageMapper;
import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.request.MessageSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.MessageRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.exception.handler.ExceptionMessage.MESSAGE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final AuthenticatedUserProvider service;
    private final ChatParticipantsService chatParticipantsService;
    private final GoogleDriveService driveService;
    private final RoomService roomService;
    private final MessageMapper messageMapper;

    @Transactional
    public MessagesDto sendMessage(MessageRequest createMessageRequest) {
        var user = userService.findUserEntityByEmail(service.getCurrentUserEmail());
        String imageThumbnailLink = driveService.getImageThumbnailLink(createMessageRequest.imageId());
        chatParticipantsService.addParticipant(createMessageRequest.roomId(), user, RoomRoles.USER);

        var savedMessage = messageRepository.save(Message.builder()
                .isEdited(Boolean.FALSE)
                .isDeleted(Boolean.FALSE)
                .content(createMessageRequest.content())
                .imageUrl(imageThumbnailLink)
                .user(user)
                .roomId(createMessageRequest.roomId())
                .build());
        return messageMapper.toMessageDto(savedMessage, roomService.findRoomByIdShort(savedMessage.getRoomId()));
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_ID, key = "#deleteRequest.messageId()"),
                    @CacheEvict(value = CacheName.MESSAGE_COUNT_BY_ROOM, allEntries = true)
            }
    )
    public void deleteMessage(MessageRequest deleteRequest) {
        messageRepository.findById(deleteRequest.messageId())
                .ifPresentOrElse(_ -> messageRepository.deleteById(deleteRequest.messageId()),
                        () -> {
                            throw new NotFoundException(MESSAGE_NOT_FOUND.formatted(deleteRequest.messageId()));
                        });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.MESSAGE_ID, key = "#id")
    public MessagesDto findById(String id) {
        return messageRepository.findById(id)
                .map(message -> messageMapper.toMessageDto(
                        message, roomService.findRoomByIdShort(message.getRoomId())))
                .orElseThrow(() -> new NotFoundException(MESSAGE_NOT_FOUND.formatted(id)));
    }

    @Transactional
    public MessagesDto editMessage(MessageRequest editRequest) {
        var message = messageRepository.findById(editRequest.messageId())
                .map(m -> {
                    m.setContent(editRequest.content());
                    m.setIsEdited(Boolean.TRUE);
                    return m;
                })
                .orElseThrow(() -> new NotFoundException(MESSAGE_NOT_FOUND.formatted(editRequest.messageId())));

        return messageMapper.toMessageDto(
                messageRepository.save(message), roomService.findRoomByIdShort(message.getRoomId()));
    }

    @Transactional(readOnly = true)
    public Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable) {
        return messageRepository.findActive(roomId, pageable)
                .map(message -> messageMapper
                        .toMessageDto(message, roomService.findRoomByIdShort(roomId)));
    }

    @Transactional(readOnly = true)
    public Page<MessagesDto> findMessagesByCriteria(MessageSearchCriteriaRequest request, Pageable pageable) {
        return messageRepository.findAll(request.buildSpecification(), pageable)
                .map(message -> messageMapper
                        .toMessageDto(message, roomService.findRoomByIdShort(request.roomId())));
    }
}
