package com.hexagonal.application.ports.input.admin.order;

import com.hexagonal.application.usecases.admin.order.HandleReturnOrRefundUseCase;
import com.hexagonal.application.ports.output.OrderRepositoryOutputPort;
import com.hexagonal.application.ports.output.PaymentRepositoryOutputPort;
import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.entity.Order;
import com.hexagonal.vo.ID;

public class HandleReturnOrRefundInputPort implements HandleReturnOrRefundUseCase {
    private final OrderRepositoryOutputPort orderRepository;
    private final PaymentRepositoryOutputPort paymentRepository;

    public HandleReturnOrRefundInputPort(OrderRepositoryOutputPort orderRepository, PaymentRepositoryOutputPort paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Order execute(ID orderId, RefundCommand command) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // basic validation
        if (command == null || command.getAmount() == null) {
            throw new IllegalArgumentException("Refund command or amount cannot be null");
        }

        // create payment refund record via paymentRepository
        paymentRepository.createRefund(orderId, command.getAmount(), command.getReason());

        // set order status to CANCELLED if refundable, otherwise keep or set appropriate status
        order.cancel();

        return orderRepository.save(order);
    }
}

