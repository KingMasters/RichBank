package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.LoginCommand;

public interface LogoutInputPort {
    void execute(LoginCommand command);
}

