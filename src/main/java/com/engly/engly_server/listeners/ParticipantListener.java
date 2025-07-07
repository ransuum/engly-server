package com.engly.engly_server.listeners;

import com.engly.engly_server.listeners.models.ChatParticipantsAddEevent;
import com.engly.engly_server.service.common.ChatParticipantsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipantListener {

    private final ChatParticipantsService chatParticipantsService;
    private final MeterRegistry meterRegistry;

    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 1000))
    public void handleAddingChatParticipant(ChatParticipantsAddEevent event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            chatParticipantsService.addParticipant(event.rooms(), event.user(), event.role());

            meterRegistry.counter("participant.added.success").increment();
            log.debug("Successfully added user as new participant {}", event.user().getEmail());
        } catch (Exception e) {
            meterRegistry.counter("participant.added.failure").increment();
            log.error("Failed to add user as new participant {}", event.user().getEmail(), e);
            throw e;
        } finally {
            sample.stop(Timer.builder("participant.added.duration").register(meterRegistry));
        }
    }

    @Recover
    public void recover(Exception ex, ChatParticipantsAddEevent event) {
        log.error("Failed to add participant after retries for user {}: {}",
                event.user().getEmail(), ex.getMessage(), ex);
    }
}
