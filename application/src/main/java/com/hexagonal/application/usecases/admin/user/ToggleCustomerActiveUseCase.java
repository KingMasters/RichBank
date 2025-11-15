package com.hexagonal.application.usecases.admin.user;

import com.hexagonal.entity.Customer;
import com.hexagonal.vo.ID;

public interface ToggleCustomerActiveUseCase {
    Customer execute(ID customerId, boolean enable);
}

