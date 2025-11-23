package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void execute(ChangePasswordCommand command);
}

