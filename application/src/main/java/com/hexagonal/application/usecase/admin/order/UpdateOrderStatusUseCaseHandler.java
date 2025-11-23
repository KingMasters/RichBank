package com.hexagonal.application.usecase.admin.order;

import com.hexagonal.application.port.in.admin.order.UpdateOrderStatusUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.OrderStatus;
import com.hexagonal.entity.Order;

public class UpdateOrderStatusUseCaseHandler implements UpdateOrderStatusUseCase {
    private final OrderRepositoryPort orderRepository;

    public UpdateOrderStatusUseCaseHandler(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(ID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // perform transition depending on newStatus
        switch (newStatus) {
            case PROCESSING -> order.startProcessing();
            case SHIPPED -> order.ship();
            case DELIVERED -> order.deliver();
            case CANCELLED -> order.cancel();
            case CONFIRMED -> order.confirm();
            case PENDING -> throw new IllegalStateException("Cannot transition back to PENDING");
            default -> throw new IllegalArgumentException("Unsupported status: " + newStatus);
        }

        return orderRepository.save(order);
    }
}

