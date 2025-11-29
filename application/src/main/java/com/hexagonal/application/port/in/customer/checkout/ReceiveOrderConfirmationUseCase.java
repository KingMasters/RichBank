package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.domain.entity.Order;

public interface ReceiveOrderConfirmationUseCase {
    Order execute(String orderId);
}

