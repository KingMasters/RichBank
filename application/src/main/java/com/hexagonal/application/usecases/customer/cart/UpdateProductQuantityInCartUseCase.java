package com.hexagonal.application.usecases.customer.cart;

import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.entity.Cart;

public interface UpdateProductQuantityInCartUseCase {
    Cart execute(UpdateProductQuantityInCartCommand command);
}

