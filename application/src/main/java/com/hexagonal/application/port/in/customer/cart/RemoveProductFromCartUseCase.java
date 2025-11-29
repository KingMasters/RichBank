package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.domain.entity.Cart;

public interface RemoveProductFromCartUseCase {
    Cart execute(RemoveProductFromCartCommand command);
}

