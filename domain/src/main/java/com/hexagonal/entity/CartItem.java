package com.hexagonal.entity;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.Quantity;
import lombok.Getter;

@Getter
public class CartItem {
    private final ID productId;
    private Quantity quantity;
    private Money unitPrice;

    private CartItem(ID productId, Quantity quantity, Money unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity == null || quantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.isNegative() || unitPrice.isZero()) {
            throw new IllegalArgumentException("Unit price must be positive");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static CartItem create(ID productId, Quantity quantity, Money unitPrice) {
        return new CartItem(productId, quantity, unitPrice);
    }

    public void updateQuantity(Quantity newQuantity) {
        if (newQuantity == null || newQuantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
    }

    public void updateUnitPrice(Money newUnitPrice) {
        if (newUnitPrice == null || newUnitPrice.isNegative() || newUnitPrice.isZero()) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        this.unitPrice = newUnitPrice;
    }

    public Money getTotalPrice() {
        return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity.getValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return productId.equals(cartItem.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}

