package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.application.port.in.customer.account.ViewOrderHistoryUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.entity.Order;
import com.hexagonal.vo.ID;

import java.util.List;

public class ViewOrderHistoryUseCaseHandler implements ViewOrderHistoryUseCase {
    private final OrderRepositoryPort orderRepository;

    public ViewOrderHistoryUseCaseHandler(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute(ViewOrderHistoryCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("ViewOrderHistoryCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        return orderRepository.findByCustomerId(customerId);
    }
}

