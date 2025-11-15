package com.hexagonal.application.ports.input.admin.order;

import com.hexagonal.application.usecases.admin.order.ViewAllOrdersUseCase;
import com.hexagonal.entity.Order;
import com.hexagonal.application.ports.output.OrderRepositoryOutputPort;

import java.util.List;

public class ViewAllOrdersInputPort implements ViewAllOrdersUseCase {
    private final OrderRepositoryOutputPort orderRepository;

    public ViewAllOrdersInputPort(OrderRepositoryOutputPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute() {
        return orderRepository.findAll();
    }
}

