package com.engly.engly_server.security.annotation;

import com.engly.engly_server.exception.RoomAccessException;
import com.engly.engly_server.exception.WebSocketException;
import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.enums.RoomAuthority;
import com.engly.engly_server.service.permission.RoomAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRoomAuthorizationAspect {
    private final RoomAuthorizationService roomAuthorizationService;

    @Around("@annotation(requireRoomPermission)")
    public Object checkRoomPermission(ProceedingJoinPoint joinPoint, RequireRoomPermission requireRoomPermission) throws Throwable {
        final var request = Arrays.stream(joinPoint.getArgs())
                .map(MessageRequest.class::cast)
                .findFirst()
                .orElseThrow(() -> new RoomAccessException("No payload provided"));

        RoomAuthority authority;
        try {
            authority = RoomAuthority.valueOf(requireRoomPermission.permission().toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new AccessDeniedException("Invalid permission: " + requireRoomPermission.permission());
        }

        final var roomId = request.roomId();
        if (!roomAuthorizationService.hasRoomPermission(roomId, authority))
            throw new AccessDeniedException("Access denied for room: " + roomId);

        return joinPoint.proceed();
    }
}
