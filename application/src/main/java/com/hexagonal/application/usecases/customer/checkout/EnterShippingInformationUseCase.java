package com.hexagonal.application.usecases.customer.checkout;

import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.entity.Cart;

public interface EnterShippingInformationUseCase {
    Cart execute(EnterShippingInformationCommand command);
}

