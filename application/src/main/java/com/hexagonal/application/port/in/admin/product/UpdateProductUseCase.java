package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.domain.entity.Product;

public interface UpdateProductUseCase {
    Product execute(UpdateProductCommand command);
}

