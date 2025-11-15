package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.entity.Customer;

public interface UpdatePersonalInformationUseCase {
    Customer execute(UpdatePersonalInformationCommand command);
}

