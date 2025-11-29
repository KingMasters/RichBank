package com.hexagonal.application.service.query.admin.order;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.order.ViewAllOrdersUseCase;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.application.port.out.OrderRepositoryPort;

import java.util.List;

@UseCase
public class ViewAllOrdersService implements ViewAllOrdersUseCase {
    private final OrderRepositoryPort orderRepository;

    public ViewAllOrdersService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute() {
        return orderRepository.findAll();
    }
}

