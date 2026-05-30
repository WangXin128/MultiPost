package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import org.springframework.stereotype.Component;

@Component
public class WechatAdapter extends AbstractPlatformAdapter {
    @Override
    public Platform getPlatform() {
        return Platform.WECHAT;
    }

    @Override
    public AdaptedContent adapt(StandardContent content) {
        String body = normalizeParagraphs(content.body());
        return new AdaptedContent(
                getPlatform(),
                shorten(content.title(), 64),
                summaryOrFallback(content, 120),
                body,
                limitTags(content.tags(), 5),
                content.sourceVersion());
    }

    @Override
    public PlatformValidationResult validate(AdaptedContent content) {
        return validateRequired(content, 64, 5);
    }
}
