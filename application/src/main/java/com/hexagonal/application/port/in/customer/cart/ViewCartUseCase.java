package com.hexagonal.application.port.in.customer.cart;

import com.hexagonal.domain.entity.Cart;

public interface ViewCartUseCase {
    Cart execute(ViewCartQuery query);

    record ViewCartQuery(String customerId) {
    }
}

