package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;
    private final SecurityService service;

    @Override
    public MessagesDto sendMessage(MessageRequestDto messageRequestDto) {
        final var name = service.getCurrentUserEmail();
        final var user = userRepo.findByEmail(name)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return roomRepo.findById(messageRequestDto.roomId())
                .map(room -> {
                    final var savedMessage = messageRepo.save(Message.builder()
                            .isEdited(Boolean.FALSE)
                            .isDeleted(Boolean.FALSE)
                            .content(messageRequestDto.content())
                            .user(user)
                            .room(room)
                            .build());
                    return MessageMapper.INSTANCE.toMessageDto(savedMessage);
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    public MessagesDto deleteMessage(String id) {
        return messageRepo.findById(id).map(message -> {
                    messageRepo.delete(message);
                    return MessageMapper.INSTANCE.toMessageDto(message);
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
                    return MessageMapper.INSTANCE.toMessageDto(messageRepo.save(message));
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
