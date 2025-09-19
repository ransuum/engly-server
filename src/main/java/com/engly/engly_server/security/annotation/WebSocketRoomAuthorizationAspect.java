package com.engly.engly_server.security.annotation;

import com.engly.engly_server.exception.RoomAccessException;
import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.enums.RoomAuthority;
import com.engly.engly_server.service.permission.RoomAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRoomAuthorizationAspect {

    private final RoomAuthorizationService roomAuthorizationService;

    private static final ScopedValue<Map<String, RoomAuthority>> ROOM_AUTHORITY_CACHE = ScopedValue.newInstance();

    @Around("@annotation(requireRoomPermission)")
    public Object checkRoomPermission(ProceedingJoinPoint joinPoint, RequireRoomPermission requireRoomPermission) throws Throwable {
        var request = Arrays.stream(joinPoint.getArgs())
                .filter(MessageRequest.class::isInstance)
                .map(MessageRequest.class::cast)
                .findFirst()
                .orElseThrow(() -> new RoomAccessException("No payload provided"));

        var roomId = request.roomId();

        return ScopedValue.where(ROOM_AUTHORITY_CACHE, new ConcurrentHashMap<>())
                .call(() -> {
                    var authority = ROOM_AUTHORITY_CACHE.get().computeIfAbsent(
                            requireRoomPermission.permission().toUpperCase(),
                            permission -> {
                                try {
                                    log.debug("Computing RoomAuthority for permission: {}", permission);
                                    return RoomAuthority.valueOf(permission);
                                } catch (IllegalArgumentException _) {
                                    throw new RoomAccessException("Invalid permission: " + requireRoomPermission.permission());
                                }
                            }
                    );

                    if (!roomAuthorizationService.hasRoomPermission(roomId, authority)) {
                        throw new RoomAccessException("Access denied for room: " + roomId);
                    }

                    return joinPoint.proceed();
                });
    }
}
