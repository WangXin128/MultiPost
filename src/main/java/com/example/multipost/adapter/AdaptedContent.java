package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import java.util.List;

public record AdaptedContent(
        Platform platform,
        String title,
        String summary,
        String body,
        List<String> tags,
        int sourceVersion) {
}
