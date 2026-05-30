package com.example.multipost.publish.dto;

import com.example.multipost.publish.PublishBatchStatus;
import java.time.Instant;
import java.util.List;

public record PublishBatchResponse(
        Long id,
        Long contentId,
        String requestId,
        PublishBatchStatus status,
        int taskCount,
        List<PublishTaskResponse> tasks,
        Instant createdAt,
        Instant updatedAt) {
}
