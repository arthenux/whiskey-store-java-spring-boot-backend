package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.ProductRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.ProductResponse;
import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> listPublicProducts(String search, String category) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String normalizedCategory = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);
        return productRepository.findByActiveTrueOrderByFeaturedDescNameAsc().stream()
                .filter(product -> normalizedSearch.isBlank()
                        || product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getShortDescription().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getDistillery().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getRegion().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .filter(product -> normalizedCategory.isBlank()
                        || product.getCategory().toLowerCase(Locale.ROOT).equals(normalizedCategory))
                .map(this::mapProduct)
                .toList();
    }

    public ProductResponse getPublicProduct(Long productId) {
        return mapProduct(productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found.")));
    }

    public List<ProductResponse> listAdminProducts() {
        return productRepository.findAllByOrderByNameAsc().stream().map(this::mapProduct).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request, null);
        return mapProduct(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found."));
        applyRequest(product, request, productId);
        return mapProduct(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found."));
        productRepository.delete(product);
    }

    public Product requireActiveProduct(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found."));
    }

    public ProductResponse mapProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getCategory(),
                product.getRegion(),
                product.getDistillery(),
                product.getAgeStatement(),
                product.getAbv(),
                product.getPrice(),
                product.getImageUrl(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getTastingNotes(),
                product.isFeatured(),
                product.isActive()
        );
    }

    private void applyRequest(Product product, ProductRequest request, Long existingProductId) {
        product.setName(request.name().trim());
        product.setSlug(resolveUniqueSlug(request.name(), existingProductId));
        product.setCategory(request.category().trim());
        product.setRegion(request.region().trim());
        product.setDistillery(request.distillery().trim());
        product.setAgeStatement(request.ageStatement() == null ? null : request.ageStatement().trim());
        product.setAbv(request.abv());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl().trim());
        product.setShortDescription(request.shortDescription().trim());
        product.setLongDescription(request.longDescription().trim());
        product.setTastingNotes(request.tastingNotes().trim());
        product.setFeatured(request.featured());
        product.setActive(request.active());
    }

    private String resolveUniqueSlug(String name, Long existingProductId) {
        String baseSlug = SlugUtils.slugify(name);
        String candidate = baseSlug.isBlank() ? "product" : baseSlug;
        int counter = 2;
        while (slugExists(candidate, existingProductId)) {
            candidate = baseSlug + "-" + counter++;
        }
        return candidate;
    }

    private boolean slugExists(String slug, Long existingProductId) {
        if (existingProductId == null) {
            return productRepository.existsBySlug(slug);
        }
        return productRepository.existsBySlugAndIdNot(slug, existingProductId);
    }
}
