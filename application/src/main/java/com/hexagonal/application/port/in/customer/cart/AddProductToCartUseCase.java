package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.domain.entity.Cart;

public interface AddProductToCartUseCase {
    Cart execute(AddProductToCartCommand command);
}

