package com.hexagonal.application.port.in.admin.user;

import com.hexagonal.entity.Customer;
import com.hexagonal.vo.ID;

public interface ToggleCustomerActiveInputPort {
    Customer execute(ID customerId, boolean enable);
}

