package com.alan.whiskey_store_java_spring_boot_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BasketItemUpdateRequest(
        @Min(1) @Max(24) int quantity
) {
}
