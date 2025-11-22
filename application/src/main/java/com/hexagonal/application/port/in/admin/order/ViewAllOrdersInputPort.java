package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.entity.Order;
import java.util.List;

public interface ViewAllOrdersInputPort {
    List<Order> execute();
}

