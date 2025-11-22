package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.entity.Product;

import java.util.List;

public interface ListAllProductsInputPort {
    List<Product> execute();
}

