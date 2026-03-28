package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketItemRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketItemResponse;
import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketItemUpdateRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.BasketResponse;
import com.alan.whiskey_store_java_spring_boot_backend.entity.BasketItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import com.alan.whiskey_store_java_spring_boot_backend.repository.BasketItemRepository;
import com.alan.whiskey_store_java_spring_boot_backend.repository.UserAccountRepository;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BasketService {

    private final BasketItemRepository basketItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProductService productService;

    public BasketService(
            BasketItemRepository basketItemRepository,
            UserAccountRepository userAccountRepository,
            ProductService productService
    ) {
        this.basketItemRepository = basketItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.productService = productService;
    }

    public BasketResponse getBasket(AuthenticatedUser authenticatedUser) {
        UserAccount user = requireUser(authenticatedUser.id());
        return toBasketResponse(basketItemRepository.findByUserOrderByCreatedAtDesc(user));
    }

    @Transactional
    public BasketResponse addItem(AuthenticatedUser authenticatedUser, BasketItemRequest request) {
        UserAccount user = requireUser(authenticatedUser.id());
        Product product = productService.requireActiveProduct(request.productId());
        BasketItem item = basketItemRepository.findByUserAndProduct(user, product).orElseGet(() -> {
            BasketItem newItem = new BasketItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(0);
            return newItem;
        });
        item.setQuantity(item.getQuantity() + request.quantity());
        basketItemRepository.save(item);
        return getBasket(authenticatedUser);
    }

    @Transactional
    public BasketResponse updateItem(AuthenticatedUser authenticatedUser, Long basketItemId, BasketItemUpdateRequest request) {
        UserAccount user = requireUser(authenticatedUser.id());
        BasketItem item = basketItemRepository.findByIdAndUser(basketItemId, user)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Basket item not found."));
        item.setQuantity(request.quantity());
        basketItemRepository.save(item);
        return getBasket(authenticatedUser);
    }

    @Transactional
    public BasketResponse removeItem(AuthenticatedUser authenticatedUser, Long basketItemId) {
        UserAccount user = requireUser(authenticatedUser.id());
        BasketItem item = basketItemRepository.findByIdAndUser(basketItemId, user)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Basket item not found."));
        basketItemRepository.delete(item);
        return getBasket(authenticatedUser);
    }

    public List<BasketItem> requireBasketItems(UserAccount user) {
        return basketItemRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void clearBasket(UserAccount user) {
        basketItemRepository.deleteAllByUser(user);
    }

    public UserAccount requireUser(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User account not found."));
    }

    private BasketResponse toBasketResponse(List<BasketItem> items) {
        List<BasketItemResponse> mappedItems = items.stream().map(item -> new BasketItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImageUrl(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        )).toList();
        BigDecimal subtotal = mappedItems.stream()
                .map(BasketItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int itemCount = mappedItems.stream().mapToInt(BasketItemResponse::quantity).sum();
        return new BasketResponse(mappedItems, itemCount, subtotal);
    }
}
