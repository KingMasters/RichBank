package com.hexagonal.application.usecases.customer.checkout;

import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.entity.Cart;

public interface SelectPaymentMethodUseCase {
    Cart execute(SelectPaymentMethodCommand command);
}

