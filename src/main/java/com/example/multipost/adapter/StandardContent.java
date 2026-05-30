package com.example.multipost.adapter;

import com.example.multipost.content.ContentType;
import java.util.List;

public record StandardContent(
        Long contentId,
        Long userId,
        String title,
        String summary,
        String body,
        List<String> tags,
        String coverUrl,
        ContentType contentType,
        int sourceVersion) {
}
