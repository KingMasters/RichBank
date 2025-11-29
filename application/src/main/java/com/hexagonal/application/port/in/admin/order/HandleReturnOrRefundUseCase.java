package com.hexagonal.application.port.in.admin.order;

import com.hexagonal.domain.vo.ID;
import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.domain.entity.Order;

public interface HandleReturnOrRefundUseCase {
    Order execute(ID orderId, RefundCommand command);
}

