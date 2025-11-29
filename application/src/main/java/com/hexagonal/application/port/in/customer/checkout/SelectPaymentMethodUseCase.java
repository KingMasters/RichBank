package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.domain.entity.Cart;

public interface SelectPaymentMethodUseCase {
    Cart execute(SelectPaymentMethodCommand command);
}

