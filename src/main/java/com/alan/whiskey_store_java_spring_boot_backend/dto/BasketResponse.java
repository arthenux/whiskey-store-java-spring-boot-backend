package com.alan.whiskey_store_java_spring_boot_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record BasketResponse(
        List<BasketItemResponse> items,
        int itemCount,
        BigDecimal subtotal
) {
}
