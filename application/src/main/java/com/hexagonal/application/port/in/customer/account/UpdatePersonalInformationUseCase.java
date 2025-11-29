package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.domain.entity.Customer;

public interface UpdatePersonalInformationUseCase {
    Customer execute(UpdatePersonalInformationCommand command);
}

