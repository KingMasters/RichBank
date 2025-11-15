package com.hexagonal.application.usecases.admin.product;

import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.entity.Product;

public interface ManageProductStockUseCase {
    Product execute(ManageStockCommand command);
}

