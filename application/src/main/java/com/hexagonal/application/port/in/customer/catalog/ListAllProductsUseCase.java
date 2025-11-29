package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.domain.entity.Product;

import java.util.List;

public interface ListAllProductsUseCase {
    List<Product> execute();
}

