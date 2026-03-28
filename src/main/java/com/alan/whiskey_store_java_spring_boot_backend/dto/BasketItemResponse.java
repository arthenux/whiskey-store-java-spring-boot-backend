package com.alan.whiskey_store_java_spring_boot_backend.dto;

import java.math.BigDecimal;

public record BasketItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
