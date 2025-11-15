package com.hexagonal.application.ports.input.customer.checkout;

import com.hexagonal.application.ports.output.OrderRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.checkout.ReceiveOrderConfirmationUseCase;
import com.hexagonal.entity.Order;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ReceiveOrderConfirmationInputPort implements ReceiveOrderConfirmationUseCase {
    private final OrderRepositoryOutputPort orderRepository;

    public ReceiveOrderConfirmationInputPort(OrderRepositoryOutputPort orderRepository) {
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

