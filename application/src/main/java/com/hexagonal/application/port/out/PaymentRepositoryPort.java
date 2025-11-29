package com.hexagonal.application.port.out;

import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;

public interface PaymentRepositoryPort {
    void createRefund(ID orderId, Money amount, String reason);
}

