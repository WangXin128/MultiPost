package com.example.multipost.publish.dto;

import com.example.multipost.platform.Platform;
import com.example.multipost.publish.PublishTaskStatus;
import java.time.Instant;

public record PublishTaskResponse(
        Long id,
        Long batchId,
        Long platformContentId,
        Platform platform,
        PublishTaskStatus status,
        int retryCount,
        String resultUrl,
        String errorMessage,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt) {
}
