package com.example.multipost.publish;

import com.example.multipost.common.BaseEntity;
import com.example.multipost.platform.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publish_tasks",
        indexes = {
                @Index(name = "idx_publish_tasks_batch", columnList = "batchId"),
                @Index(name = "idx_publish_tasks_status", columnList = "status")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_publish_task_platform",
                columnNames = {"batchId", "platform"}))
public class PublishTask extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long batchId;

    @Column(nullable = false)
    private Long platformContentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishTaskStatus status = PublishTaskStatus.PENDING;

    @Column(nullable = false)
    private int retryCount = 0;

    @Column(length = 500)
    private String resultUrl;

    @Lob
    private String errorMessage;

    private Instant publishedAt;
}
