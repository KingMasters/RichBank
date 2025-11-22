package com.hexagonal.application.port.in.customer.catalog;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.entity.Product;

import java.util.List;

public interface FilterProductsByCategoryInputPort {
    List<Product> execute(FilterProductsByCategoryCommand command);
}

