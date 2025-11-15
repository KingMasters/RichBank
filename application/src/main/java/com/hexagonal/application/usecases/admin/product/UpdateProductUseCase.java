package com.hexagonal.application.usecases.admin.product;

import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.entity.Product;

public interface UpdateProductUseCase {
    Product execute(UpdateProductCommand command);
}

