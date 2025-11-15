package com.hexagonal.application.usecases.customer.catalog;

import com.hexagonal.entity.Product;

import java.util.List;

public interface ListAllProductsUseCase {
    List<Product> execute();
}

