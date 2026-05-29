package com.example.multipost.auth;

import com.example.multipost.auth.dto.AuthResponse;
import com.example.multipost.auth.dto.LoginRequest;
import com.example.multipost.auth.dto.RegisterRequest;
import com.example.multipost.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthUserProvider authUserProvider;

    public AuthController(AuthService authService, AuthUserProvider authUserProvider) {
        this.authService = authService;
        this.authUserProvider = authUserProvider;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUser> me() {
        return ApiResponse.ok(authUserProvider.currentUser());
    }
}
