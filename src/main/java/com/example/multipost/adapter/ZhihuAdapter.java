package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import org.springframework.stereotype.Component;

@Component
public class ZhihuAdapter extends AbstractPlatformAdapter {
    @Override
    public Platform getPlatform() {
        return Platform.ZHIHU;
    }

    @Override
    public AdaptedContent adapt(StandardContent content) {
        String body = "Conclusion first:\n\n" + normalizeParagraphs(content.body());
        return new AdaptedContent(
                getPlatform(),
                shorten(content.title(), 80),
                summaryOrFallback(content, 160),
                body,
                limitTags(content.tags(), 8),
                content.sourceVersion());
    }

    @Override
    public PlatformValidationResult validate(AdaptedContent content) {
        return validateRequired(content, 80, 8);
    }
}
