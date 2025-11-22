package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.entity.Customer;

public interface RegisterAccountInputPort {
    Customer execute(RegisterAccountCommand command);
}

