package com.hexagonal.application.port.in.customer.account;

import com.hexagonal.domain.entity.Order;

import java.util.List;

public interface ViewOrderHistoryUseCase {
    List<Order> execute(ViewOrderHistoryQuery query);

    record ViewOrderHistoryQuery(String customerId) {

    }
}

