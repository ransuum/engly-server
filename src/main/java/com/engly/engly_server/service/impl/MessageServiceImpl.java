package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.EventType;
import com.engly.engly_server.models.request.create.MessageRequest;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.MessageService;
import com.engly.engly_server.websocket.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;
    private final SecurityService service;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String TOPIC_MESSAGES = "/topic/messages/";

    @Override
    public MessagesDto sendMessage(MessageRequest messageRequest) {
        final var name = service.getCurrentUserEmail();
        final var user = userRepo.findByEmail(name)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return roomRepo.findById(messageRequest.roomId())
                .map(room -> {
                    final var savedMessage = messageRepo.save(Message.builder()
                            .isEdited(Boolean.FALSE)
                            .isDeleted(Boolean.FALSE)
                            .content(messageRequest.content())
                            .user(user)
                            .room(room)
                            .build());
                    final var message = MessageMapper.INSTANCE.toMessageDto(savedMessage);
                    simpMessagingTemplate.convertAndSend(
                            TOPIC_MESSAGES + messageRequest.roomId(),
                            new WebSocketEvent<>(EventType.MESSAGE_SEND, message));
                    return message;
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    public void deleteMessage(String id) {
        messageRepo.findById(id).map(message -> {
            messageRepo.delete(message);
            final var messageDto = MessageMapper.INSTANCE.toMessageDto(message);
            simpMessagingTemplate.convertAndSend(
                    TOPIC_MESSAGES + messageDto.room().id(),
                    new WebSocketEvent<>(EventType.MESSAGE_DELETE, messageDto));

            return MessageMapper.INSTANCE.toMessageDto(message);
        }).orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
                    final var messageDto = MessageMapper.INSTANCE.toMessageDto(messageRepo.save(message));
                    simpMessagingTemplate.convertAndSend(
                            TOPIC_MESSAGES + messageDto.room().id(),
                            new WebSocketEvent<>(EventType.MESSAGE_EDIT, messageDto));
                    return messageDto;
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    public Page<MessagesDto> findAllMessageInCurrentRoom(String roomId, Pageable pageable) {
        return messageRepo.findAllByRoomId(roomId, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.findAllMessagesByRoomIdContainingKeyString(roomId, keyString, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }
}
