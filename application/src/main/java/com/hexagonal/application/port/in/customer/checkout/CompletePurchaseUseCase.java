package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.entity.Order;

public interface CompletePurchaseUseCase {
    Order execute(CompletePurchaseCommand command);
}

