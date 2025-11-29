package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.domain.entity.Customer;

public interface RegisterAccountUseCase {
    Customer execute(RegisterAccountCommand command);
}

