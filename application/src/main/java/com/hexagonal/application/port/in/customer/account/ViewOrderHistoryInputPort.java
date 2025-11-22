package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.entity.Order;

import java.util.List;

public interface ViewOrderHistoryInputPort {
    List<Order> execute(ViewOrderHistoryCommand command);
}

