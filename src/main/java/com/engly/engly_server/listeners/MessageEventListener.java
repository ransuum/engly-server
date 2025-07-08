package com.engly.engly_server.listeners;

import com.engly.engly_server.listeners.models.MessagesViewedEvent;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.service.common.MessageReadService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class MessageEventListener {

    private final MessageReadService messageReadService;
    private final MeterRegistry meterRegistry;

    @Async("messageReadExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 1000))
    public void handleMessagesViewed(MessagesViewedEvent event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.debug("Marking {} messages as read for user {}",
                    event.messages().size(), event.userId());

            final var messageIds = event.messages().stream()
                    .map(Message::getId)
                    .toList();
            messageReadService.markMessageAsRead(messageIds, event.userId());

            meterRegistry.counter("message.viewed.success").increment();
            log.debug("Successfully marked messages as read for user {}", event.userId());

        } catch (Exception e) {
            meterRegistry.counter("message.viewed.failure").increment();
            log.error("Failed to mark messages as read for user {}: {}",
                    event.userId(), e.getMessage(), e);
            throw e;
        } finally {
            sample.stop(Timer.builder("message.viewed.duration").register(meterRegistry));
        }
    }

    @Recover
    public void recover(Exception ex, MessagesViewedEvent event) {
        log.error("Failed to process MessagesViewedEvent after retries for user {}: {}",
                event.userId(), ex.getMessage(), ex);
        meterRegistry.counter("message.read.abandoned").increment();
    }
}
