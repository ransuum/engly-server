package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/room")
    public WebSocketMessage sendMessage(WebSocketMessage message) {
        return message;
    }
}
