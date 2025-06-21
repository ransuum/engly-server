package com.engly.engly_server.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        final var accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwt = accessor.getFirstNativeHeader("Authorization");

            if (jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
                Authentication auth = jwtTokenUtils.validateToken(jwt);
                accessor.setUser(auth);
            }
        } else {
            final var user = accessor.getUser();
            if (user instanceof Authentication authentication) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }

        return message;
    }

    @Override
    public void afterSendCompletion(@NotNull Message<?> message, @NotNull MessageChannel channel,
                                    boolean sent, Exception ex) {
        SecurityContextHolder.clearContext();
    }
}
