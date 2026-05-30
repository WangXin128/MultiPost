package com.example.multipost.auth;

import com.example.multipost.auth.dto.AuthResponse;
import com.example.multipost.auth.dto.LoginRequest;
import com.example.multipost.auth.dto.RegisterRequest;
import com.example.multipost.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Register, login, and current user APIs")
public class AuthController {
    private final AuthService authService;
    private final AuthUserProvider authUserProvider;

    public AuthController(AuthService authService, AuthUserProvider authUserProvider) {
        this.authService = authService;
        this.authUserProvider = authUserProvider;
    }

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(summary = "Register a new user")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login and receive a JWT")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ApiResponse<CurrentUser> me() {
        return ApiResponse.ok(authUserProvider.currentUser());
    }
}
