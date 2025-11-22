package com.hexagonal.application.usecase.customer.checkout;

import com.hexagonal.application.port.in.customer.checkout.ReceiveOrderConfirmationInputPort;
import com.hexagonal.application.port.out.OrderRepositoryOutputPort;
import com.hexagonal.entity.Order;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ReceiveOrderConfirmationUseCase implements ReceiveOrderConfirmationInputPort {
    private final OrderRepositoryOutputPort orderRepository;

    public ReceiveOrderConfirmationUseCase(OrderRepositoryOutputPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        ID id = ID.of(orderId);
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id));
    }
}

