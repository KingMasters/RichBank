package com.hexagonal.application.ports.input.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.usecases.customer.account.LogoutUseCase;

public class LogoutInputPort implements LogoutUseCase {
    public LogoutInputPort() {
    }

    @Override
    public void execute(LoginCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("LoginCommand cannot be null");
        }

        // Note: Logout logic would typically be handled by a session management service
        // This is a simplified version for the hexagon structure
    }
}

