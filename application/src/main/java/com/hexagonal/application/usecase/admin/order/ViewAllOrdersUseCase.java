package com.hexagonal.application.usecase.admin.order;

import com.hexagonal.entity.Order;
import com.hexagonal.application.port.out.OrderRepositoryOutputPort;

import java.util.List;

public class ViewAllOrdersUseCase implements com.hexagonal.application.port.in.admin.order.ViewAllOrdersInputPort {
    private final OrderRepositoryOutputPort orderRepository;

    public ViewAllOrdersUseCase(OrderRepositoryOutputPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute() {
        return orderRepository.findAll();
    }
}

