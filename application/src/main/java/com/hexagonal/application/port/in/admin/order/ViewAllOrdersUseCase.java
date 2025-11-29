package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.domain.entity.Order;
import java.util.List;

public interface ViewAllOrdersUseCase {
    List<Order> execute();
}

