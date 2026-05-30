package com.example.multipost.publish;

import com.example.multipost.common.ApiResponse;
import com.example.multipost.publish.dto.PublishBatchCreateRequest;
import com.example.multipost.publish.dto.PublishBatchResponse;
import com.example.multipost.publish.dto.PublishTaskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publish")
public class PublishBatchController {
    private final PublishBatchService publishBatchService;

    public PublishBatchController(PublishBatchService publishBatchService) {
        this.publishBatchService = publishBatchService;
    }

    @PostMapping("/batches")
    public ApiResponse<PublishBatchResponse> create(@Valid @RequestBody PublishBatchCreateRequest request) {
        return ApiResponse.ok(publishBatchService.create(request));
    }

    @GetMapping("/batches/{id}")
    public ApiResponse<PublishBatchResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(publishBatchService.get(id));
    }

    @GetMapping("/tasks/{id}")
    public ApiResponse<PublishTaskResponse> getTask(@PathVariable Long id) {
        return ApiResponse.ok(publishBatchService.getTask(id));
    }
}
