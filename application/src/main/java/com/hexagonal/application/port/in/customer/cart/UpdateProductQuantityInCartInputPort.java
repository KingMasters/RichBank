package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.entity.Cart;

public interface UpdateProductQuantityInCartInputPort {
    Cart execute(UpdateProductQuantityInCartCommand command);
}

