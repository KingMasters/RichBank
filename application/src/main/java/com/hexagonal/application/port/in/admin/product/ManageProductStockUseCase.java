package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.domain.entity.Product;

public interface ManageProductStockUseCase {
    Product execute(ManageStockCommand command);
}

