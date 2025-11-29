package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.domain.entity.Order;

import java.util.List;

public interface ViewOrderHistoryUseCase {
    List<Order> execute(ViewOrderHistoryCommand command);
}

