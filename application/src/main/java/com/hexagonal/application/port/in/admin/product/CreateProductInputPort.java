package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.entity.Product;

public interface CreateProductInputPort {
    Product execute(CreateProductCommand command);
}

