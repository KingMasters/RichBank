package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.entity.Customer;

public interface UpdatePersonalInformationInputPort {
    Customer execute(UpdatePersonalInformationCommand command);
}

