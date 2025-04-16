package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.request.create.MessageRequest;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.MessageService;
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
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessagesDto sendMessage(MessageRequest messageRequest) {
        var name = service.getCurrentUserEmail();
        var user = userRepo.findByEmail(name)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return roomRepo.findById(messageRequest.roomId())
                .map(room -> {
                    var savedMessage = messageRepo.save(Message.builder()
                            .isEdited(Boolean.FALSE)
                            .isDeleted(Boolean.FALSE)
                            .content(messageRequest.content())
                            .user(user)
                            .room(room)
                            .build());

                    var messageDto = MessageMapper.INSTANCE.toMessageDto(savedMessage);
                    messagingTemplate.convertAndSend("/topic/" + messageRequest.roomId(), messageDto);
                    return messageDto;
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    public void deleteMessage(String id) {
        messageRepo.deleteById(id);
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
        return messageRepo.findAllByRoomId(roomId, pageable).map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.findAllMessagesByRoomIdContainingKeyString(roomId, keyString, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }
}
