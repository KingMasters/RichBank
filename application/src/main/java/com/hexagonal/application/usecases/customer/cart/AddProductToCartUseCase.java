package com.hexagonal.application.usecases.customer.cart;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.entity.Cart;

public interface AddProductToCartUseCase {
    Cart execute(AddProductToCartCommand command);
}

