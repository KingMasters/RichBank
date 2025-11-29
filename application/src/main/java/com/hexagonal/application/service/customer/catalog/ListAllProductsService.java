package com.hexagonal.application.service.customer.catalog;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@UseCase
public class ListAllProductsService implements ListAllProductsUseCase {
    private final ProductRepositoryPort productRepository;

    public ListAllProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
}

