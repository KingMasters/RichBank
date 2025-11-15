package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.LoginCommand;

public interface LogoutUseCase {
    void execute(LoginCommand command);
}

