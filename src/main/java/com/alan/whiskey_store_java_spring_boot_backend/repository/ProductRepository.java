package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByFeaturedDescNameAsc();

    List<Product> findAllByOrderByNameAsc();

    Optional<Product> findByIdAndActiveTrue(Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
