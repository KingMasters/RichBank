package com.hexagonal.application.port.out;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;

public interface PaymentRepositoryPort {
    void createRefund(ID orderId, Money amount, String reason);
}

