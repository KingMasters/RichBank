package com.hexagonal.application.usecase.customer.catalog;

import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class SearchProductsUseCase implements com.hexagonal.application.port.in.customer.catalog.SearchProductsInputPort {
    private final ProductRepositoryOutputPort productRepository;

    public SearchProductsUseCase(ProductRepositoryOutputPort productRepository) {
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

