package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.application.dto.ApplyDiscountCodeCommand;
import com.hexagonal.entity.Cart;

public interface ApplyDiscountCodeInputPort {
    Cart execute(ApplyDiscountCodeCommand command);
}

