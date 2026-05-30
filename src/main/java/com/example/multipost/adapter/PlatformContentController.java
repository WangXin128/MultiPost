package com.example.multipost.adapter;

import com.example.multipost.adapter.dto.AdaptContentRequest;
import com.example.multipost.adapter.dto.PlatformContentResponse;
import com.example.multipost.adapter.dto.PlatformContentUpdateRequest;
import com.example.multipost.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Platform Content", description = "Platform-specific adaptation and editing APIs")
public class PlatformContentController {
    private final PlatformContentService platformContentService;

    public PlatformContentController(PlatformContentService platformContentService) {
        this.platformContentService = platformContentService;
    }

    @PostMapping("/api/contents/{contentId}/adapt")
    @Operation(summary = "Generate platform-specific content")
    public ApiResponse<List<PlatformContentResponse>> adapt(
            @PathVariable Long contentId,
            @RequestBody(required = false) AdaptContentRequest request) {
        return ApiResponse.ok(platformContentService.adapt(contentId, request));
    }

    @GetMapping("/api/contents/{contentId}/platform-contents")
    @Operation(summary = "List adapted platform contents for an original content")
    public ApiResponse<List<PlatformContentResponse>> listByContent(@PathVariable Long contentId) {
        return ApiResponse.ok(platformContentService.listByContent(contentId));
    }

    @PutMapping("/api/platform-contents/{id}")
    @Operation(summary = "Manually edit adapted platform content")
    public ApiResponse<PlatformContentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PlatformContentUpdateRequest request) {
        return ApiResponse.ok(platformContentService.update(id, request));
    }
}
