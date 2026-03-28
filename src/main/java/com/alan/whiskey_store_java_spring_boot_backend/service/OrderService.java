package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.CheckoutRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.OrderItemResponse;
import com.alan.whiskey_store_java_spring_boot_backend.dto.OrderResponse;
import com.alan.whiskey_store_java_spring_boot_backend.entity.BasketItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.CustomerOrder;
import com.alan.whiskey_store_java_spring_boot_backend.entity.OrderItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.OrderStatus;
import com.alan.whiskey_store_java_spring_boot_backend.repository.CustomerOrderRepository;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class OrderService {

    private static final BigDecimal SHIPPING_COST = new BigDecimal("9.95");

    private final BasketService basketService;
    private final CustomerOrderRepository customerOrderRepository;
    private final StoreInfoService storeInfoService;

    public OrderService(
            BasketService basketService,
            CustomerOrderRepository customerOrderRepository,
            StoreInfoService storeInfoService
    ) {
        this.basketService = basketService;
        this.customerOrderRepository = customerOrderRepository;
        this.storeInfoService = storeInfoService;
    }

    public List<OrderResponse> listOrders(AuthenticatedUser authenticatedUser) {
        UserAccount user = basketService.requireUser(authenticatedUser.id());
        return customerOrderRepository.findByUserOrderByCreatedAtDesc(user).stream().map(this::mapOrder).toList();
    }

    @Transactional
    public OrderResponse checkout(AuthenticatedUser authenticatedUser, CheckoutRequest request) {
        UserAccount user = basketService.requireUser(authenticatedUser.id());
        List<BasketItem> basketItems = basketService.requireBasketItems(user);
        if (basketItems.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Your basket is empty.");
        }
        storeInfoService.validateTestCard(request.payment());

        BigDecimal subtotal = basketItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setOrderNumber("WS-" + Instant.now().toEpochMilli());
        order.setStatus(OrderStatus.PAID);
        order.setSubtotal(subtotal);
        order.setShippingCost(SHIPPING_COST);
        order.setTotal(subtotal.add(SHIPPING_COST));
        order.setShippingName(request.shippingName().trim());
        order.setShippingEmail(request.shippingEmail().trim());
        order.setShippingAddressLine1(request.shippingAddressLine1().trim());
        order.setShippingAddressLine2(request.shippingAddressLine2() == null ? null : request.shippingAddressLine2().trim());
        order.setShippingCity(request.shippingCity().trim());
        order.setShippingPostcode(request.shippingPostcode().trim());
        order.setShippingCountry(request.shippingCountry().trim());

        for (BasketItem basketItem : basketItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(basketItem.getProduct().getId());
            orderItem.setProductName(basketItem.getProduct().getName());
            orderItem.setProductImageUrl(basketItem.getProduct().getImageUrl());
            orderItem.setUnitPrice(basketItem.getProduct().getPrice());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setLineTotal(basketItem.getProduct().getPrice().multiply(BigDecimal.valueOf(basketItem.getQuantity())));
            order.addItem(orderItem);
        }

        CustomerOrder savedOrder = customerOrderRepository.save(order);
        basketService.clearBasket(user);
        return mapOrder(savedOrder);
    }

    private OrderResponse mapOrder(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getTotal(),
                order.getShippingName(),
                order.getShippingEmail(),
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingPostcode(),
                order.getShippingCountry(),
                order.getCreatedAt(),
                order.getItems().stream().map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductImageUrl(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getLineTotal()
                )).toList()
        );
    }
}
