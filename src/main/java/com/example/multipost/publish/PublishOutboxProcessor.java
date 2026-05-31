package com.example.multipost.publish;

import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishOutboxProcessor {
    private final PublishOutboxRepository publishOutboxRepository;
    private final PublishTaskDispatcher publishTaskDispatcher;
    private final int maxAttempts;

    public PublishOutboxProcessor(
            PublishOutboxRepository publishOutboxRepository,
            PublishTaskDispatcher publishTaskDispatcher,
            @Value("${multipost.outbox.max-attempts:5}") int maxAttempts) {
        this.publishOutboxRepository = publishOutboxRepository;
        this.publishTaskDispatcher = publishTaskDispatcher;
        this.maxAttempts = maxAttempts;
    }

    @Scheduled(fixedDelayString = "${multipost.outbox.fixed-delay-ms:1000}")
    @Transactional
    public void processDueEvents() {
        List<PublishOutbox> events = publishOutboxRepository.findDue(PublishOutboxStatus.PENDING, Instant.now());
        events.stream().limit(50).forEach(this::processOne);
    }

    private void processOne(PublishOutbox event) {
        try {
            if (!PublishOutboxService.EVENT_PUBLISH_TASK.equals(event.getEventType())) {
                throw new IllegalArgumentException("unsupported outbox event type: " + event.getEventType());
            }
            publishTaskDispatcher.dispatch(event.getAggregateId());
            event.setStatus(PublishOutboxStatus.SENT);
            event.setLastError(null);
        } catch (Exception ex) {
            int nextRetryCount = event.getRetryCount() + 1;
            event.setRetryCount(nextRetryCount);
            event.setLastError(ex.getMessage());
            if (nextRetryCount >= maxAttempts) {
                event.setStatus(PublishOutboxStatus.FAILED);
                return;
            }
            event.setNextRetryAt(Instant.now().plusSeconds(Math.min(60, 5L * nextRetryCount)));
        }
    }
}
