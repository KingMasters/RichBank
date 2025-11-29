package com.hexagonal.application.service.customer.catalog;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service - List All Products Use Case Implementation
 *
 * Query Service:
 * - Repository'den tüm ürünleri alır
 * - Sadece aktif ürünleri filtreler
 */
@UseCase
public class ListAllProductsService implements ListAllProductsUseCase {
    private final ProductRepositoryPort productRepository;

    public ListAllProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Tüm ürünleri listeleme use case'i
     * - Aktif olan ürünleri al
     */
    @Override
    public List<Product> execute() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
}

