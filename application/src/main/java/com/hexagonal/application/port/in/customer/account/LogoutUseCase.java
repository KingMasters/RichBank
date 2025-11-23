package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.LoginCommand;

public interface LogoutUseCase {
    void execute(LoginCommand command);
}

