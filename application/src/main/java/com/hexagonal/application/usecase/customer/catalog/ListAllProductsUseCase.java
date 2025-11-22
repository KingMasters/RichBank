package com.hexagonal.application.usecase.customer.catalog;

import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ListAllProductsUseCase implements com.hexagonal.application.port.in.customer.catalog.ListAllProductsInputPort {
    private final ProductRepositoryOutputPort productRepository;

    public ListAllProductsUseCase(ProductRepositoryOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
}

