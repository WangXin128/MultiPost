package com.example.multipost.content.dto;

import com.example.multipost.content.ContentStatus;
import com.example.multipost.content.ContentType;
import java.time.Instant;

public record ContentResponse(
        Long id,
        String title,
        String summary,
        String body,
        String tags,
        String coverUrl,
        ContentType contentType,
        ContentStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {
}
