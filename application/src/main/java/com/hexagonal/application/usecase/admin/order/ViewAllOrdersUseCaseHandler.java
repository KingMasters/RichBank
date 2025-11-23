package com.hexagonal.application.usecase.admin.order;

import com.hexagonal.application.port.in.admin.order.ViewAllOrdersUseCase;
import com.hexagonal.entity.Order;
import com.hexagonal.application.port.out.OrderRepositoryPort;

import java.util.List;

public class ViewAllOrdersUseCaseHandler implements ViewAllOrdersUseCase {
    private final OrderRepositoryPort orderRepository;

    public ViewAllOrdersUseCaseHandler(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute() {
        return orderRepository.findAll();
    }
}

