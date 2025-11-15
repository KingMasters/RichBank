package com.hexagonal.application.usecases.admin.order;

import com.hexagonal.vo.ID;
import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.entity.Order;

public interface HandleReturnOrRefundUseCase {
    Order execute(ID orderId, RefundCommand command);
}

