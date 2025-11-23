package com.hexagonal.application.usecase.customer.catalog;

import com.hexagonal.application.port.in.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ListAllProductsUseCaseHandler implements ListAllProductsUseCase {
    private final ProductRepositoryPort productRepository;

    public ListAllProductsUseCaseHandler(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
}

