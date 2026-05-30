package com.example.multipost.adapter.dto;

import com.example.multipost.platform.Platform;
import java.time.Instant;
import java.util.List;

public record PlatformContentResponse(
        Long id,
        Long contentId,
        Platform platform,
        String title,
        String summary,
        String body,
        List<String> tags,
        int sourceVersion,
        Instant createdAt,
        Instant updatedAt) {
}
