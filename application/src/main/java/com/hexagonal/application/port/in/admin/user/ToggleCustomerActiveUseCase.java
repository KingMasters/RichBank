package com.hexagonal.application.port.in.admin.user;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.ID;

public interface ToggleCustomerActiveUseCase {
    Customer execute(ID customerId, boolean enable);
}

