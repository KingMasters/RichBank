package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.entity.Customer;

public interface LoginUseCase {
    Customer execute(LoginCommand command);
}

