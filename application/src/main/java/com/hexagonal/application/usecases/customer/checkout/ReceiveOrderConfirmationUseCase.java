package com.hexagonal.application.usecases.customer.checkout;

import com.hexagonal.entity.Order;

public interface ReceiveOrderConfirmationUseCase {
    Order execute(String orderId);
}

