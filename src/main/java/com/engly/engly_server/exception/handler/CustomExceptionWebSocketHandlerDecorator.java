package com.engly.engly_server.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import java.io.IOException;

import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.SERVER_ERROR;
import static org.springframework.web.socket.CloseStatus.BAD_DATA;
import static org.springframework.web.socket.CloseStatus.POLICY_VIOLATION;

@Slf4j
public class CustomExceptionWebSocketHandlerDecorator extends ExceptionWebSocketHandlerDecorator {
    public CustomExceptionWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage(), exception);
        try {
            final var status = mapExceptionToStatus(exception);
            if (session.isOpen()) session.close(status);
        } catch (IOException e) {
            log.error("Failed to close WebSocket session: {}", e.getMessage());
        }
        super.handleTransportError(session, exception);
    }

    private CloseStatus mapExceptionToStatus(Throwable exception) {
        return switch (exception) {
            case IllegalArgumentException e -> BAD_DATA.withReason("Invalid data: " + e.getMessage());
            case SecurityException e -> POLICY_VIOLATION.withReason("Unauthorized: " + e.getMessage());
            default -> new CloseStatus(1011, SERVER_ERROR);
        };
    }

}
