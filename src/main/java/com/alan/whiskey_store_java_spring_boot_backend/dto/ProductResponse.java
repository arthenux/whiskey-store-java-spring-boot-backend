package com.alan.whiskey_store_java_spring_boot_backend.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String category,
        String region,
        String distillery,
        String ageStatement,
        BigDecimal abv,
        BigDecimal price,
        String imageUrl,
        String shortDescription,
        String longDescription,
        String tastingNotes,
        boolean featured,
        boolean active
) {
}
