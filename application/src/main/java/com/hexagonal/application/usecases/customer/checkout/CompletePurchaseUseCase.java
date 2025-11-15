package com.hexagonal.application.usecases.customer.checkout;

import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.entity.Order;

public interface CompletePurchaseUseCase {
    Order execute(CompletePurchaseCommand command);
}

