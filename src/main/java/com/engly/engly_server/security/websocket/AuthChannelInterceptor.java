package com.engly.engly_server.security.websocket;

import com.engly.engly_server.exception.WebSocketException;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        final var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.debug("Channel Interceptor: Command={}, Headers={}", Objects.requireNonNull(accessor).getCommand(), accessor.getMessageHeaders());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader(AUTHORIZATION_HEADER);
            log.debug("Authorization header: {}", authorization);

            if (authorization == null || authorization.isEmpty()) {
                log.warn("No Authorization header found in STOMP CONNECT frame.");
                return message;
            }

            String authToken = authorization.getFirst();
            if (authToken != null && authToken.startsWith(BEARER_PREFIX)) {
                authToken = authToken.substring(BEARER_PREFIX.length());
                try {
                    final var authentication = jwtTokenUtils.createAuthentication(authToken);

                    accessor.setUser(authentication);
                    log.info("Authenticated user {} for WebSocket session.", authentication.getName());

                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                    throw new WebSocketException("Authentication failed");
                }
            }
        }
        return message;
    }
}
