package com.example.multipost.publish.dto;

import com.example.multipost.platform.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PublishBatchCreateRequest(
        @NotNull Long contentId,
        @NotBlank @Size(max = 80) String requestId,
        List<Platform> platforms) {
}
