package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.domain.entity.Product;

import java.util.List;

public interface SearchProductsUseCase {
    List<Product> execute(SearchProductsCommand command);
}

