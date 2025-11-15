package com.hexagonal.application.usecases.customer.catalog;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface FilterProductsByCategoryUseCase {
    List<Product> execute(FilterProductsByCategoryCommand command);
}

