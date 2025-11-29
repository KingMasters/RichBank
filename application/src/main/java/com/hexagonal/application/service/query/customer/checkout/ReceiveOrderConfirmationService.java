package com.hexagonal.application.service.query.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.customer.checkout.ReceiveOrderConfirmationUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ReceiveOrderConfirmationService implements ReceiveOrderConfirmationUseCase {
    private final OrderRepositoryPort orderRepository;

    public ReceiveOrderConfirmationService(OrderRepositoryPort orderRepository) {
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

