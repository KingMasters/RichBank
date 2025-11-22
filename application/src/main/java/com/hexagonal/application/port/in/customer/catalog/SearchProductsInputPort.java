package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface SearchProductsInputPort {
    List<Product> execute(SearchProductsCommand command);
}

