package com.example.multipost.publish;

import com.example.multipost.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publish_outbox", indexes = {
        @Index(name = "idx_publish_outbox_status", columnList = "status,nextRetryAt"),
        @Index(name = "idx_publish_outbox_aggregate", columnList = "aggregateId")
})
public class PublishOutbox extends BaseEntity {
    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false)
    private Long aggregateId;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishOutboxStatus status = PublishOutboxStatus.PENDING;

    @Column(nullable = false)
    private int retryCount = 0;

    private Instant nextRetryAt;

    @Lob
    private String lastError;
}
