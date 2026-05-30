package com.example.multipost.publish;

import com.example.multipost.common.ApiResponse;
import com.example.multipost.publish.dto.PublishBatchCreateRequest;
import com.example.multipost.publish.dto.PublishBatchResponse;
import com.example.multipost.publish.dto.PublishTaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publish")
@Tag(name = "Publish", description = "Publish batch, task status, and retry APIs")
public class PublishBatchController {
    private final PublishBatchService publishBatchService;

    public PublishBatchController(PublishBatchService publishBatchService) {
        this.publishBatchService = publishBatchService;
    }

    @PostMapping("/batches")
    @Operation(summary = "Create a publish batch and dispatch platform tasks")
    public ApiResponse<PublishBatchResponse> create(@Valid @RequestBody PublishBatchCreateRequest request) {
        return ApiResponse.ok(publishBatchService.create(request));
    }

    @GetMapping("/batches/{id}")
    @Operation(summary = "Get publish batch status and task list")
    public ApiResponse<PublishBatchResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(publishBatchService.get(id));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get single publish task status")
    public ApiResponse<PublishTaskResponse> getTask(@PathVariable Long id) {
        return ApiResponse.ok(publishBatchService.getTask(id));
    }

    @PostMapping("/tasks/{id}/retry")
    @Operation(summary = "Retry a failed publish task")
    public ApiResponse<PublishTaskResponse> retryTask(@PathVariable Long id) {
        return ApiResponse.ok(publishBatchService.retryTask(id));
    }
}
