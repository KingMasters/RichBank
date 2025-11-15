package com.hexagonal.application.usecases.admin.order;

import com.hexagonal.entity.Order;
import java.util.List;

public interface ViewAllOrdersUseCase {
    List<Order> execute();
}

