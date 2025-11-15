package com.hexagonal.application.ports.input.admin.order;

import com.hexagonal.application.usecases.admin.order.UpdateOrderStatusUseCase;
import com.hexagonal.application.ports.output.OrderRepositoryOutputPort;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.OrderStatus;
import com.hexagonal.entity.Order;

public class UpdateOrderStatusInputPort implements UpdateOrderStatusUseCase {
    private final OrderRepositoryOutputPort orderRepository;

    public UpdateOrderStatusInputPort(OrderRepositoryOutputPort orderRepository) {
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

