package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.domain.entity.Cart;

public interface UpdateProductQuantityInCartUseCase {
    Cart execute(UpdateProductQuantityInCartCommand command);
}

