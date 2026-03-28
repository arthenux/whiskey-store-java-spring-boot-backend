package com.alan.whiskey_store_java_spring_boot_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BasketItemRequest(
        @NotNull Long productId,
        @Min(1) @Max(24) int quantity
) {
}
