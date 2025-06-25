package com.engly.engly_server.exception;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import java.io.IOException;

@Slf4j
public class CustomExceptionWebSocketHandlerDecorator extends ExceptionWebSocketHandlerDecorator {
    public CustomExceptionWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage(), exception);
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(exception.getMessage()));
        } catch (IOException e) {
            log.error("Failed to close WebSocket session", e);
        }
        super.handleTransportError(session, exception);
    }
}
