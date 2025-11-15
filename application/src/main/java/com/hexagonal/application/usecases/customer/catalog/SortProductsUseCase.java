package com.hexagonal.application.usecases.customer.catalog;

import com.hexagonal.application.dto.SortProductsCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface SortProductsUseCase {
    List<Product> execute(List<Product> products, SortProductsCommand command);
}

