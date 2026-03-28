package com.alan.whiskey_store_java_spring_boot_backend.security;

import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.Role;

public record AuthenticatedUser(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
