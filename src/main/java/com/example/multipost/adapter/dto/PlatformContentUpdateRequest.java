package com.example.multipost.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PlatformContentUpdateRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 500) String summary,
        @NotBlank String body,
        @Size(max = 20) List<@Size(max = 40) String> tags) {
}
