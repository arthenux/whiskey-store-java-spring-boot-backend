package com.alan.whiskey_store_java_spring_boot_backend.controller;

import com.alan.whiskey_store_java_spring_boot_backend.dto.CheckoutRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.OrderResponse;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import com.alan.whiskey_store_java_spring_boot_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> listOrders(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return orderService.listOrders(authenticatedUser);
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CheckoutRequest request
    ) {
        return orderService.checkout(authenticatedUser, request);
    }
}
