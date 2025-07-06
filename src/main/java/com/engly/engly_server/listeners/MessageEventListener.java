package com.engly.engly_server.listeners;

import com.engly.engly_server.models.dto.MessagesViewedEvent;
import com.engly.engly_server.service.common.MessageReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEventListener {

    private final MessageReadService messageReadService;

    @EventListener
    @Async("messageViewedExecutor")
    public void handleMessagesViewed(MessagesViewedEvent event) {
        try {
            log.debug("Marking {} messages as read for user {}",
                    event.messageIds().size(), event.userId());

            messageReadService.markMessageAsRead(event.messageIds(), event.userId());

            log.debug("Successfully marked messages as read for user {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to mark messages as read for user {}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }
}
