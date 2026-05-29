package com.example.multipost.auth;

import com.example.multipost.user.UserAccount;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final String secret;
    private final long expirationMinutes;

    public JwtService(
            @Value("${multipost.jwt-secret}") String secret,
            @Value("${multipost.jwt-expiration-minutes}") long expirationMinutes) {
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(UserAccount user) {
        long expiresAt = Instant.now().plusSeconds(expirationMinutes * 60).getEpochSecond();
        String payload = user.getId() + ":" + user.getEmail() + ":" + user.getUsername() + ":" + expiresAt;
        String payloadPart = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return payloadPart + "." + sign(payloadPart);
    }

    public CurrentUser parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            throw new IllegalArgumentException("invalid token");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String[] values = payload.split(":", 4);
        if (values.length != 4) {
            throw new IllegalArgumentException("invalid token payload");
        }
        long expiresAt = Long.parseLong(values[3]);
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new IllegalArgumentException("token expired");
        }
        return new CurrentUser(Long.parseLong(values[0]), values[1], values[2]);
    }

    private String sign(String payloadPart) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(payloadPart.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("could not sign token", ex);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
