package com.example.multipost.auth;

import com.example.multipost.auth.dto.AuthResponse;
import com.example.multipost.auth.dto.LoginRequest;
import com.example.multipost.auth.dto.RegisterRequest;
import com.example.multipost.user.UserAccount;
import com.example.multipost.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            throw new IllegalArgumentException("email already registered");
        }
        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByEmailAndDeletedFalse(request.email().toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("invalid email or password");
        }
        return toResponse(user);
    }

    private AuthResponse toResponse(UserAccount user) {
        return new AuthResponse(jwtService.generate(user), user.getId(), user.getEmail(), user.getUsername());
    }
}
