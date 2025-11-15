package com.hexagonal.application.ports.input.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.application.ports.output.OrderRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.account.ViewOrderHistoryUseCase;
import com.hexagonal.entity.Order;
import com.hexagonal.vo.ID;

import java.util.List;

public class ViewOrderHistoryInputPort implements ViewOrderHistoryUseCase {
    private final OrderRepositoryOutputPort orderRepository;

    public ViewOrderHistoryInputPort(OrderRepositoryOutputPort orderRepository) {
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

