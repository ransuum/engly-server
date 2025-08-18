package com.engly.engly_server.security.websocket;

import com.engly.engly_server.exception.WebSocketException;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            final var authorization = accessor.getNativeHeader(HttpHeaders.AUTHORIZATION);

            if (authorization == null || authorization.isEmpty())
                return message;

            var authToken = authorization.getFirst();
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
                try {
                    final var authentication = jwtTokenUtils.createSocketAuthentication(authToken);

                    accessor.setUser(authentication);
                } catch (Exception e) {
                    throw new WebSocketException("Authentication failed: " + e.getMessage());
                }
            }
        }
        return message;
    }
}
