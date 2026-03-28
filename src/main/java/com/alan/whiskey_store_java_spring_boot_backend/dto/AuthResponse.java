package com.alan.whiskey_store_java_spring_boot_backend.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        Instant expiresAt,
        UserResponse user
) {
}
