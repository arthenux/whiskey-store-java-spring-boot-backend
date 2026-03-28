package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.FavoriteItem;
import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {

    List<FavoriteItem> findByUserOrderByCreatedAtDesc(UserAccount user);

    Optional<FavoriteItem> findByUserAndProduct(UserAccount user, Product product);

    boolean existsByUserAndProduct(UserAccount user, Product product);
}
