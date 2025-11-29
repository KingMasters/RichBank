package com.hexagonal.domain.entity;

import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.Quantity;
import lombok.Getter;

@Getter
public class OrderItem {
    private final ID productId;
    private final String productName;
    private final Quantity quantity;
    private final Money unitPrice;
    private final Money totalPrice;

    private OrderItem(ID productId, String productName, Quantity quantity, Money unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (quantity == null || quantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.isNegative() || unitPrice.isZero()) {
            throw new IllegalArgumentException("Unit price must be positive");
        }

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity.getValue()));
    }

    public static OrderItem create(ID productId, String productName, Quantity quantity, Money unitPrice) {
        return new OrderItem(productId, productName, quantity, unitPrice);
    }
}

