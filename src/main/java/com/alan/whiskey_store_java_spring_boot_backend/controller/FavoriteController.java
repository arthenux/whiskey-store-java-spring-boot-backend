package com.alan.whiskey_store_java_spring_boot_backend.controller;

import com.alan.whiskey_store_java_spring_boot_backend.dto.ProductResponse;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import com.alan.whiskey_store_java_spring_boot_backend.service.FavoriteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<ProductResponse> listFavorites(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return favoriteService.listFavorites(authenticatedUser);
    }

    @PostMapping("/{productId}")
    public List<ProductResponse> addFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long productId
    ) {
        return favoriteService.addFavorite(authenticatedUser, productId);
    }

    @DeleteMapping("/{productId}")
    public List<ProductResponse> removeFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long productId
    ) {
        return favoriteService.removeFavorite(authenticatedUser, productId);
    }
}
