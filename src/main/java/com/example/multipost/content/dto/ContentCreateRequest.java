package com.example.multipost.content.dto;

import com.example.multipost.content.ContentStatus;
import com.example.multipost.content.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContentCreateRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 500) String summary,
        @NotBlank String body,
        @Size(max = 500) String tags,
        @Size(max = 500) String coverUrl,
        @NotNull ContentType contentType,
        ContentStatus status) {
}
