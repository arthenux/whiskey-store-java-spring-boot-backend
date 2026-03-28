package com.alan.whiskey_store_java_spring_boot_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 180) String name,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 80) String region,
        @NotBlank @Size(max = 120) String distillery,
        @Size(max = 40) String ageStatement,
        @NotNull @DecimalMin("0.00") BigDecimal abv,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @NotBlank String imageUrl,
        @NotBlank @Size(max = 320) String shortDescription,
        @NotBlank String longDescription,
        @NotBlank String tastingNotes,
        boolean featured,
        boolean active
) {
}
