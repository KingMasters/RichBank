package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.entity.Cart;

public interface RemoveProductFromCartInputPort {
    Cart execute(RemoveProductFromCartCommand command);
}

