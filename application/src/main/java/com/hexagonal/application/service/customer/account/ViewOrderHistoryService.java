package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.application.port.in.customer.account.ViewOrderHistoryUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.vo.ID;

import java.util.List;

@UseCase
public class ViewOrderHistoryService implements ViewOrderHistoryUseCase {
    private final OrderRepositoryPort orderRepository;

    public ViewOrderHistoryService(OrderRepositoryPort orderRepository) {
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

