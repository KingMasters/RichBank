package com.hexagonal.application.ports.output;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;

public interface PaymentRepositoryOutputPort {
    void createRefund(ID orderId, Money amount, String reason);
}

