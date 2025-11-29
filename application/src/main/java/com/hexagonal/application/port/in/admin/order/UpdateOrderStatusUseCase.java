package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.OrderStatus;
import com.hexagonal.domain.entity.Order;

public interface UpdateOrderStatusUseCase {
    Order execute(ID orderId, OrderStatus newStatus);
}

