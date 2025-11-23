package com.hexagonal.application.usecase.customer.catalog;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.application.port.in.customer.catalog.FilterProductsByCategoryUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.List;
import java.util.stream.Collectors;

public class FilterProductsByCategoryUseCaseHandler implements FilterProductsByCategoryUseCase {
    private final ProductRepositoryPort productRepository;

    public FilterProductsByCategoryUseCaseHandler(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

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

