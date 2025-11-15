package com.hexagonal.application.usecases.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.entity.Order;

import java.util.List;

public interface ViewOrderHistoryUseCase {
    List<Order> execute(ViewOrderHistoryCommand command);
}

