package com.example.multipost.publish;

import com.example.multipost.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publish_batches",
        indexes = @Index(name = "idx_publish_batches_user", columnList = "userId"),
        uniqueConstraints = @UniqueConstraint(name = "uk_publish_batch_idempotency",
                columnNames = {"userId", "requestId"}))
public class PublishBatch extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 80)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishBatchStatus status = PublishBatchStatus.PUBLISHING;

    @Column(nullable = false)
    private int taskCount;
}
