package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void execute(ChangePasswordCommand command);
}

