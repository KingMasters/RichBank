package com.hexagonal.application.ports.input.customer.catalog;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.catalog.FilterProductsByCategoryUseCase;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.List;
import java.util.stream.Collectors;

public class FilterProductsByCategoryInputPort implements FilterProductsByCategoryUseCase {
    private final ProductRepositoryOutputPort productRepository;

    public FilterProductsByCategoryInputPort(ProductRepositoryOutputPort productRepository) {
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

