package com.example.multipost.platform;

import com.example.multipost.common.ApiResponse;
import com.example.multipost.platform.dto.PlatformCapabilityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform-capabilities")
@Tag(name = "Platform Capability", description = "Platform publish mode and integration capability matrix")
public class PlatformCapabilityController {
    private final PlatformCapabilityService platformCapabilityService;

    public PlatformCapabilityController(PlatformCapabilityService platformCapabilityService) {
        this.platformCapabilityService = platformCapabilityService;
    }

    @GetMapping
    @Operation(summary = "List enabled platform capabilities")
    public ApiResponse<List<PlatformCapabilityResponse>> listEnabled() {
        return ApiResponse.ok(platformCapabilityService.listEnabled());
    }
}
