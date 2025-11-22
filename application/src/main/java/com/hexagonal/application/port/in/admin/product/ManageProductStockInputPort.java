package com.hexagonal.application.port.in.admin.product;

import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.entity.Product;

public interface ManageProductStockInputPort {
    Product execute(ManageStockCommand command);
}

