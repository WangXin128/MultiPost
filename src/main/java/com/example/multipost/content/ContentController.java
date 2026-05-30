package com.example.multipost.content;

import com.example.multipost.common.ApiResponse;
import com.example.multipost.content.dto.ContentCreateRequest;
import com.example.multipost.content.dto.ContentResponse;
import com.example.multipost.content.dto.ContentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
@Tag(name = "Content", description = "Original content draft management")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    @Operation(summary = "Create original content")
    public ApiResponse<ContentResponse> create(@Valid @RequestBody ContentCreateRequest request) {
        return ApiResponse.ok(contentService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get original content detail")
    public ApiResponse<ContentResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(contentService.get(id));
    }

    @GetMapping
    @Operation(summary = "List original contents")
    public ApiResponse<Page<ContentResponse>> list(Pageable pageable) {
        return ApiResponse.ok(contentService.list(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update original content and increment version")
    public ApiResponse<ContentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ContentUpdateRequest request) {
        return ApiResponse.ok(contentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete original content")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        contentService.delete(id);
        return ApiResponse.ok(null);
    }
}
