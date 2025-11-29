package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.domain.entity.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}

