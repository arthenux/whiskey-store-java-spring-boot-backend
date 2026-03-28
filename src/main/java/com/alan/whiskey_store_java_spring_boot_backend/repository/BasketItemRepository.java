package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.BasketItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    List<BasketItem> findByUserOrderByCreatedAtDesc(UserAccount user);

    Optional<BasketItem> findByUserAndProduct(UserAccount user, Product product);

    Optional<BasketItem> findByIdAndUser(Long id, UserAccount user);

    void deleteAllByUser(UserAccount user);
}
