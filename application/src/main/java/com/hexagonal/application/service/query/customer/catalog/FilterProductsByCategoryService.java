package com.hexagonal.application.service.query.customer.catalog;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.application.port.in.customer.catalog.FilterProductsByCategoryUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service - Filter Products By Category Use Case Implementation
 *
 * Query Service:
 * - Repository'den tüm ürünleri alır
 * - Kategori ve durum bazında filtreler
 */
@UseCase
public class FilterProductsByCategoryService implements FilterProductsByCategoryUseCase {
    private final ProductRepositoryPort productRepository;

    public FilterProductsByCategoryService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Ürünleri kategoriye göre filtreleme use case'i
     * - Aktif ürünleri kategori bazında filtrele
     */
    @Override
    public List<Product> execute(FilterProductsByCategoryCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("FilterProductsByCategoryCommand cannot be null");
        }

        ID categoryId = ID.of(command.getCategoryId());
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .filter(product -> product.hasCategory(categoryId))
                .collect(Collectors.toList());
    }
}

