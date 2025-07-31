package com.engly.engly_server.security.root;

import com.engly.engly_server.models.dto.create.CreateMessageData;
import com.engly.engly_server.models.dto.create.TypingRequest;
import com.engly.engly_server.models.enums.RoomAuthority;
import com.engly.engly_server.service.permission.RoomAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRoomAuthorizationAspect {
    private final RoomAuthorizationService roomAuthorizationService;

    @Around("@annotation(requireRoomPermission)")
    public Object checkRoomPermission(ProceedingJoinPoint joinPoint, RequireRoomPermission requireRoomPermission) throws Throwable {
        final var args = joinPoint.getArgs();

        if (args.length == 0) throw new AccessDeniedException("No payload provided");

        final var roomId = extractRoomIdFromPayload(args[0]);
        if (roomId == null) throw new AccessDeniedException("Could not extract roomId from payload");

        RoomAuthority authority;
        try {
            authority = RoomAuthority.valueOf(requireRoomPermission.permission().toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new AccessDeniedException("Invalid permission: " + requireRoomPermission.permission());
        }

        if (!roomAuthorizationService.hasRoomPermission(roomId, authority))
            throw new AccessDeniedException("Access denied for room: " + roomId);

        return joinPoint.proceed();
    }

    private String extractRoomIdFromPayload(Object payload) {
        return switch (payload) {
            case CreateMessageData dto -> dto.roomId();
            case TypingRequest dto -> dto.roomId();
            default -> null;
        };
    }
}
