package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import org.springframework.stereotype.Component;

@Component
public class BilibiliAdapter extends AbstractPlatformAdapter {
    @Override
    public Platform getPlatform() {
        return Platform.BILIBILI;
    }

    @Override
    public AdaptedContent adapt(StandardContent content) {
        String summary = summaryOrFallback(content, 200);
        String body = summary + "\n\n" + "Full notes:\n" + normalizeParagraphs(content.body());
        return new AdaptedContent(
                getPlatform(),
                shorten(content.title(), 60),
                summary,
                body,
                limitTags(content.tags(), 10),
                content.sourceVersion());
    }

    @Override
    public PlatformValidationResult validate(AdaptedContent content) {
        return validateRequired(content, 60, 10);
    }
}
