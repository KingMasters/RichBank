package com.hexagonal.domain.specification;

import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.OrderStatus;

public class OrderSpecifications {
    
    public static Specification<Order> hasStatus(OrderStatus status) {
        return order -> order.getStatus().equals(status);
    }

    public static Specification<Order> belongsToCustomer(ID customerId) {
        return order -> order.getCustomerId().equals(customerId);
    }

    public static Specification<Order> totalAmountGreaterThan(Money amount) {
        return order -> order.getTotalAmount().isGreaterThan(amount) || order.getTotalAmount().equals(amount);
    }

    public static Specification<Order> canBeCancelled() {
        return Order::canBeCancelled;
    }

    public static Specification<Order> isCompleted() {
        return Order::isCompleted;
    }

    public static Specification<Order> isPending() {
        return hasStatus(OrderStatus.PENDING);
    }

    public static Specification<Order> isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }
}

