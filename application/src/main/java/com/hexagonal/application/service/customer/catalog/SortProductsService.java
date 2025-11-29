package com.hexagonal.application.service.customer.catalog;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.SortProductsCommand;
import com.hexagonal.application.port.in.customer.catalog.SortProductsUseCase;
import com.hexagonal.domain.entity.Product;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UseCase
public class SortProductsService implements SortProductsUseCase {
    public SortProductsService() {
    }

    @Override
    public List<Product> execute(List<Product> products, SortProductsCommand command) {
        if (products == null) {
            throw new IllegalArgumentException("Products list cannot be null");
        }
        if (command == null) {
            throw new IllegalArgumentException("SortProductsCommand cannot be null");
        }

        Comparator<Product> comparator = getComparator(command);
        
        if ("desc".equalsIgnoreCase(command.getSortOrder())) {
            comparator = comparator.reversed();
        }

        return products.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<Product> getComparator(SortProductsCommand command) {
        String sortBy = command.getSortBy().toLowerCase();
        
        switch (sortBy) {
            case "price":
                return Comparator.comparing(product -> product.getPrice().getAmount());
            case "name":
                return Comparator.comparing(Product::getName);
            case "popularity":
                // Note: Popularity would typically be based on order count or views
                // For now, we'll use a default sort by name
                return Comparator.comparing(Product::getName);
            default:
                throw new IllegalArgumentException("Invalid sort by field: " + command.getSortBy());
        }
    }
}

