package com.hexagonal.application.usecases.customer.catalog;

import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface SearchProductsUseCase {
    List<Product> execute(SearchProductsCommand command);
}

