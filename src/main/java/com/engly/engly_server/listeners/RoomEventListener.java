package com.engly.engly_server.listeners;

import com.engly.engly_server.listeners.models.RoomCreatedEvent;
import com.engly.engly_server.service.common.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomEventListener {

    private final RoomService roomService;

    @EventListener
    public void onRoomCreated(RoomCreatedEvent event) {

    }
}
