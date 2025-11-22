package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.entity.Order;

public interface ReceiveOrderConfirmationInputPort {
    Order execute(String orderId);
}

