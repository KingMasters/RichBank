package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.application.port.out.OrderRepositoryOutputPort;
import com.hexagonal.entity.Order;
import com.hexagonal.vo.ID;

import java.util.List;

public class ViewOrderHistoryUseCase implements com.hexagonal.application.port.in.customer.account.ViewOrderHistoryInputPort {
    private final OrderRepositoryOutputPort orderRepository;

    public ViewOrderHistoryUseCase(OrderRepositoryOutputPort orderRepository) {
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

