package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.vo.ID;
import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.entity.Order;

public interface HandleReturnOrRefundInputPort {
    Order execute(ID orderId, RefundCommand command);
}

