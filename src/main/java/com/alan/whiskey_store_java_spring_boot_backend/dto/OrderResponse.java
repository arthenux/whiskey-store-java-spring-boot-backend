package com.alan.whiskey_store_java_spring_boot_backend.dto;

import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal total,
        String shippingName,
        String shippingEmail,
        String shippingAddressLine1,
        String shippingAddressLine2,
        String shippingCity,
        String shippingPostcode,
        String shippingCountry,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
