package com.example.multipost.platform.dto;

import com.example.multipost.platform.AuthType;
import com.example.multipost.platform.Platform;
import com.example.multipost.platform.PublishMode;

public record PlatformCapabilityResponse(
        Platform platform,
        String displayName,
        PublishMode publishMode,
        AuthType authType,
        boolean supportsSchedule,
        boolean supportsStatusQuery,
        boolean supportsMediaUpload,
        boolean enabled,
        String notes) {
}
