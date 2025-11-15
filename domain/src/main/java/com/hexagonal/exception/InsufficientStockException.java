package com.hexagonal.exception;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.Quantity;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(ID productId, Quantity requested, Quantity available) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                productId, requested.getValue(), available.getValue()));
    }
}

