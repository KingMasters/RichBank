package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.in.customer.account.LogoutUseCase;

@UseCase
public class LogoutService implements LogoutUseCase {
    public LogoutService() {
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

