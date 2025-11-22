package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.entity.Product;

public interface UpdateProductInputPort {
    Product execute(UpdateProductCommand command);
}

