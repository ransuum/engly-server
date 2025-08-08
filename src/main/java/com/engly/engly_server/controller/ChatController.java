package com.engly.engly_server.controller;

import com.engly.engly_server.listeners.models.TypingEvent;
import com.engly.engly_server.models.dto.create.TypingRequest;
import com.engly.engly_server.models.enums.EventType;
import com.engly.engly_server.models.dto.create.CreateMessageData;
import com.engly.engly_server.models.dto.update.EditMessageRequest;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.root.RequireRoomPermission;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.config.websocket.WebSocketEvent;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_WRITE')")
public class ChatController {
    private final MessageService messageService;
    private final UserService userService;
    private final SecurityService securityService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_MESSAGES = "/topic/messages/";

    @MessageMapping("/chat/message.send")
    public void sendMessage(@Payload CreateMessageData createMessageData) {
        final var message = messageService.sendMessage(createMessageData);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_SEND, message));
    }

    @MessageMapping("/chat/message.edit")
    @RequireRoomPermission(permission = "ROOM_WRITE")
    public void editMessage(@Payload EditMessageRequest request) {
        final var message = messageService.editMessage(request.id(), request.content());
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + request.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_EDIT, message));
    }

    @MessageMapping("/chat/message.delete")
    @RequireRoomPermission(permission = "ROOM_WRITE")
    public void deleteMessage(@Payload String id) {
        messageService.deleteMessage(id);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + id,
                new WebSocketEvent<>(EventType.MESSAGE_DELETE,
                        "Message with ID " + id + " has been deleted"));
    }

    @MessageMapping("/chat/user.typing")
    public void userTyping(@Payload TypingRequest typingRequest) {
        final var username = userService.getUsernameByEmail(securityService.getCurrentUserEmail());
        final var typingEvent = new TypingEvent(
                typingRequest.roomId(),
                username,
                typingRequest.isTyping(),
                Instant.now()
        );

        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + typingRequest.roomId(),
                new WebSocketEvent<>(EventType.USER_TYPING, typingEvent)
        );
    }
}
