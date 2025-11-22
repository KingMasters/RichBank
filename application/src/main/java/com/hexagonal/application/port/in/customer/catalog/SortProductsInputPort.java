package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.application.dto.SortProductsCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface SortProductsInputPort {
    List<Product> execute(List<Product> products, SortProductsCommand command);
}

