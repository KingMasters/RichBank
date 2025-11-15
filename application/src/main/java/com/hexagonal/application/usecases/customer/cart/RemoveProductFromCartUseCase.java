package com.hexagonal.application.usecases.customer.cart;

import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.entity.Cart;

public interface RemoveProductFromCartUseCase {
    Cart execute(RemoveProductFromCartCommand command);
}

