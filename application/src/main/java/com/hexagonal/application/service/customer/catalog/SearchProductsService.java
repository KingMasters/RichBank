package com.hexagonal.application.service.customer.catalog;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.application.port.in.customer.catalog.SearchProductsUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@UseCase
public class SearchProductsService implements SearchProductsUseCase {
    private final ProductRepositoryPort productRepository;

    public SearchProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute(SearchProductsCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SearchProductsCommand cannot be null");
        }

        String searchTerm = command.getSearchTerm().toLowerCase().trim();
        if (searchTerm.isBlank()) {
            return List.of();
        }

        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .filter(product -> 
                    product.getName().toLowerCase().contains(searchTerm) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(searchTerm))
                )
                .collect(Collectors.toList());
    }
}

