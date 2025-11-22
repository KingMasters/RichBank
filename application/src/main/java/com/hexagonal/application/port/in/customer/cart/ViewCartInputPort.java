package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.entity.Cart;

public interface ViewCartInputPort {
    Cart execute(ViewCartCommand command);
}

