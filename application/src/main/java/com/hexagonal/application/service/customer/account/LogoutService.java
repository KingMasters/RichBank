package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.in.customer.account.LogoutUseCase;

/**
 * Application Service - Logout Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Oturum sonlandırma işlemini handle eder
 * - Session yönetimi normalde bir port tarafından yapılabilir
 */
@UseCase
public class LogoutService implements LogoutUseCase {
    public LogoutService() {
    }

    /**
     * Oturum sonlandırma use case'i
     * NOT: Gerçek uygulamada session management service tarafından yapılmalıdır
     */
    @Override
    public void execute(LoginCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("LoginCommand cannot be null");
        }

        // Logout işlemi burada yapılır
        // Session yönetimi normalde başka bir service tarafından handle edilir
    }
}

