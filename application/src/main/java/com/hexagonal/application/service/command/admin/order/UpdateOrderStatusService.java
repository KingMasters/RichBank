package com.hexagonal.application.service.command.admin.order;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.order.UpdateOrderStatusUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.OrderStatus;
import com.hexagonal.domain.entity.Order;

@UseCase
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {
    private final OrderRepositoryPort orderRepository;

    public UpdateOrderStatusService(OrderRepositoryPort orderRepository) {
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

