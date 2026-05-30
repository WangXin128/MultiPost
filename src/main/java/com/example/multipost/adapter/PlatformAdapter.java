package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;

public interface PlatformAdapter {
    Platform getPlatform();

    AdaptedContent adapt(StandardContent content);

    PlatformValidationResult validate(AdaptedContent content);
}
