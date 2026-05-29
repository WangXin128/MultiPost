package com.example.multipost.publish;

import com.example.multipost.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publish_logs", indexes = @Index(name = "idx_publish_logs_task", columnList = "taskId"))
public class PublishLog extends BaseEntity {
    @Column(nullable = false)
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishTaskStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishTaskStatus toStatus;

    @Lob
    private String message;
}
