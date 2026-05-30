package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import org.springframework.stereotype.Component;

@Component
public class XiaohongshuAdapter extends AbstractPlatformAdapter {
    @Override
    public Platform getPlatform() {
        return Platform.XIAOHONGSHU;
    }

    @Override
    public AdaptedContent adapt(StandardContent content) {
        String body = normalizeParagraphs(content.body())
                + "\n\n"
                + String.join(" ", hashtagTags(content.tags(), 10));
        return new AdaptedContent(
                getPlatform(),
                shorten(content.title(), 20),
                summaryOrFallback(content, 80),
                body,
                hashtagTags(content.tags(), 10),
                content.sourceVersion());
    }

    @Override
    public PlatformValidationResult validate(AdaptedContent content) {
        return validateRequired(content, 20, 10);
    }
}
