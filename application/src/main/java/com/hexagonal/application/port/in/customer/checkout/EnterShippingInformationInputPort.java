package com.hexagonal.application.port.in.customer.checkout;

import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.entity.Cart;

public interface EnterShippingInformationInputPort {
    Cart execute(EnterShippingInformationCommand command);
}

