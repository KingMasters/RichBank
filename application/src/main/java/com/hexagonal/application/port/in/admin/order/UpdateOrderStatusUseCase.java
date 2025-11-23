package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.OrderStatus;
import com.hexagonal.entity.Order;

public interface UpdateOrderStatusUseCase {
    Order execute(ID orderId, OrderStatus newStatus);
}

