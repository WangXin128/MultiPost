package com.example.multipost.publish;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PublishOutboxService {
    public static final String EVENT_PUBLISH_TASK = "PUBLISH_TASK";

    private final PublishOutboxRepository publishOutboxRepository;

    public PublishOutboxService(PublishOutboxRepository publishOutboxRepository) {
        this.publishOutboxRepository = publishOutboxRepository;
    }

    public void enqueuePublishTask(Long taskId) {
        enqueuePublishTask(taskId, null);
    }

    public void enqueuePublishTask(Long taskId, Instant runAt) {
        PublishOutbox outbox = new PublishOutbox();
        outbox.setEventType(EVENT_PUBLISH_TASK);
        outbox.setAggregateId(taskId);
        outbox.setPayload("{\"taskId\":" + taskId + "}");
        outbox.setStatus(PublishOutboxStatus.PENDING);
        outbox.setNextRetryAt(runAt);
        publishOutboxRepository.save(outbox);
    }
}
