package com.hexagonal.application.ports.input.customer.catalog;

import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ListAllProductsInputPort implements ListAllProductsUseCase {
    private final ProductRepositoryOutputPort productRepository;

    public ListAllProductsInputPort(ProductRepositoryOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
}

