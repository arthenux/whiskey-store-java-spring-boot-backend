package com.alan.whiskey_store_java_spring_boot_backend.controller;

import com.alan.whiskey_store_java_spring_boot_backend.dto.ProductResponse;
import com.alan.whiskey_store_java_spring_boot_backend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        return productService.listPublicProducts(search, category);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.getPublicProduct(productId);
    }
}
