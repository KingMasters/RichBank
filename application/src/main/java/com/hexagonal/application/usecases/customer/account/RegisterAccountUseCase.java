package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.entity.Customer;

public interface RegisterAccountUseCase {
    Customer execute(RegisterAccountCommand command);
}

