package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.entity.Customer;

public interface LoginUseCase {
    Customer execute(LoginCommand command);
}

