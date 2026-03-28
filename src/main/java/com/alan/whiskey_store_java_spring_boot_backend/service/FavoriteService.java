package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.ProductResponse;
import com.alan.whiskey_store_java_spring_boot_backend.entity.FavoriteItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import com.alan.whiskey_store_java_spring_boot_backend.repository.FavoriteItemRepository;
import com.alan.whiskey_store_java_spring_boot_backend.repository.UserAccountRepository;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FavoriteService {

    private final FavoriteItemRepository favoriteItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProductService productService;

    public FavoriteService(
            FavoriteItemRepository favoriteItemRepository,
            UserAccountRepository userAccountRepository,
            ProductService productService
    ) {
        this.favoriteItemRepository = favoriteItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.productService = productService;
    }

    public List<ProductResponse> listFavorites(AuthenticatedUser authenticatedUser) {
        UserAccount user = requireUser(authenticatedUser.id());
        return favoriteItemRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(FavoriteItem::getProduct)
                .map(productService::mapProduct)
                .toList();
    }

    @Transactional
    public List<ProductResponse> addFavorite(AuthenticatedUser authenticatedUser, Long productId) {
        UserAccount user = requireUser(authenticatedUser.id());
        Product product = productService.requireActiveProduct(productId);
        if (!favoriteItemRepository.existsByUserAndProduct(user, product)) {
            FavoriteItem favoriteItem = new FavoriteItem();
            favoriteItem.setUser(user);
            favoriteItem.setProduct(product);
            favoriteItemRepository.save(favoriteItem);
        }
        return listFavorites(authenticatedUser);
    }

    @Transactional
    public List<ProductResponse> removeFavorite(AuthenticatedUser authenticatedUser, Long productId) {
        UserAccount user = requireUser(authenticatedUser.id());
        Product product = productService.requireActiveProduct(productId);
        favoriteItemRepository.findByUserAndProduct(user, product).ifPresent(favoriteItemRepository::delete);
        return listFavorites(authenticatedUser);
    }

    private UserAccount requireUser(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User account not found."));
    }
}
