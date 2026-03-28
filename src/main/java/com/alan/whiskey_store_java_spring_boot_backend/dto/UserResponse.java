package com.alan.whiskey_store_java_spring_boot_backend.dto;

import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}
