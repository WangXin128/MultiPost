package com.example.multipost.adapter;

import com.example.multipost.adapter.dto.AdaptContentRequest;
import com.example.multipost.adapter.dto.PlatformContentResponse;
import com.example.multipost.adapter.dto.PlatformContentUpdateRequest;
import com.example.multipost.common.ApiResponse;
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
public class PlatformContentController {
    private final PlatformContentService platformContentService;

    public PlatformContentController(PlatformContentService platformContentService) {
        this.platformContentService = platformContentService;
    }

    @PostMapping("/api/contents/{contentId}/adapt")
    public ApiResponse<List<PlatformContentResponse>> adapt(
            @PathVariable Long contentId,
            @RequestBody(required = false) AdaptContentRequest request) {
        return ApiResponse.ok(platformContentService.adapt(contentId, request));
    }

    @GetMapping("/api/contents/{contentId}/platform-contents")
    public ApiResponse<List<PlatformContentResponse>> listByContent(@PathVariable Long contentId) {
        return ApiResponse.ok(platformContentService.listByContent(contentId));
    }

    @PutMapping("/api/platform-contents/{id}")
    public ApiResponse<PlatformContentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PlatformContentUpdateRequest request) {
        return ApiResponse.ok(platformContentService.update(id, request));
    }
}
