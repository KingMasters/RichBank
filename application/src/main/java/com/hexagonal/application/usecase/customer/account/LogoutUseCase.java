package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.LoginCommand;

public class LogoutUseCase implements com.hexagonal.application.port.in.customer.account.LogoutInputPort {
    public LogoutUseCase() {
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

