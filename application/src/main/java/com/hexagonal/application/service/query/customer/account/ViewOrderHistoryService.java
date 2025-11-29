package com.hexagonal.application.service.query.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.customer.account.ViewOrderHistoryUseCase;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.vo.ID;

import java.util.List;

/**
 * Application Service - View Order History Use Case Implementation
 *
 * Query Service:
 * - Repository'den siparişleri alır
 * - Müşterinin sipariş geçmişini döndürür
 */
@UseCase
public class ViewOrderHistoryService implements ViewOrderHistoryUseCase {
    private final OrderRepositoryPort orderRepository;

    public ViewOrderHistoryService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Siparış geçmişini görüntüleme use case'i
     * - Müşteri kimliğine göre sipariş listesini al
     */
    @Override
    public List<Order> execute(ViewOrderHistoryQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("ViewOrderHistoryCommand cannot be null");
        }

        ID customerId = ID.of(query.customerId());
        return orderRepository.findByCustomerId(customerId);
    }
}

