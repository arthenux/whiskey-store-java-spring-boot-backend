package com.alan.whiskey_store_java_spring_boot_backend.controller;

import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketItemRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketItemUpdateRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketResponse;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import com.alan.whiskey_store_java_spring_boot_backend.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping
    public BasketResponse getBasket(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return basketService.getBasket(authenticatedUser);
    }

    @PostMapping("/items")
    public BasketResponse addItem(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody BasketItemRequest request
    ) {
        return basketService.addItem(authenticatedUser, request);
    }

    @PatchMapping("/items/{basketItemId}")
    public BasketResponse updateItem(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long basketItemId,
            @Valid @RequestBody BasketItemUpdateRequest request
    ) {
        return basketService.updateItem(authenticatedUser, basketItemId, request);
    }

    @DeleteMapping("/items/{basketItemId}")
    public BasketResponse removeItem(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long basketItemId
    ) {
        return basketService.removeItem(authenticatedUser, basketItemId);
    }
}
