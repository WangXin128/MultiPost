package com.example.multipost.auth.dto;

public record AuthResponse(String token, Long userId, String email, String username) {
}
