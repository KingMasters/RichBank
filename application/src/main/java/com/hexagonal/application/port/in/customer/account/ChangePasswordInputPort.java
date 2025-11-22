package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.ChangePasswordCommand;

public interface ChangePasswordInputPort {
    void execute(ChangePasswordCommand command);
}

