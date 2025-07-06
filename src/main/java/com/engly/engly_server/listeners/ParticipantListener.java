package com.engly.engly_server.listeners;

import com.engly.engly_server.listeners.models.ParticipantAddedEvent;
import com.engly.engly_server.models.dto.create.ChatParticipantsRequestDto;
import com.engly.engly_server.service.common.ChatParticipantsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipantListener {

    private final ChatParticipantsService chatParticipantsService;

    @EventListener
    @Async("chatParticipantsExecutor")
    public void handleAddingChatParticipant(ParticipantAddedEvent event) {
        try {
            chatParticipantsService.addParticipant(new ChatParticipantsRequestDto(event.rooms(), event.users(), event.roles()));

            log.debug("Successfully added user as new participant {}", event.users().getEmail());
        } catch (Exception e) {
            log.error("Failed to add user as new participant {}", event.users().getEmail(), e);
        }
    }
}
