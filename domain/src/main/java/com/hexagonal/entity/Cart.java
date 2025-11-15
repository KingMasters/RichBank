package com.hexagonal.entity;

import com.hexagonal.vo.CartStatus;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.Quantity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart {
    private final ID id;
    private final ID customerId;
    private List<CartItem> items;
    private CartStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Cart(ID id, ID customerId) {
        if (id == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = CartStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Cart create(ID customerId) {
        return new Cart(ID.generate(), customerId);
    }

    public static Cart of(ID id, ID customerId) {
        return new Cart(id, customerId);
    }

    public static Cart of(ID id, ID customerId, CartStatus status) {
        Cart cart = new Cart(id, customerId);
        cart.status = status;
        return cart;
    }

    public void addItem(ID productId, Quantity quantity, Money unitPrice) {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add item to cart with status: " + status);
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity == null || quantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }

        Optional<CartItem> existingItem = findItemByProductId(productId);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.updateQuantity(item.getQuantity().add(quantity));
            item.updateUnitPrice(unitPrice);
        } else {
            items.add(CartItem.create(productId, quantity, unitPrice));
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateItemQuantity(ID productId, Quantity newQuantity) {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cannot update item in cart with status: " + status);
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (newQuantity == null || newQuantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));
        item.updateQuantity(newQuantity);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeItem(ID productId) {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cannot remove item from cart with status: " + status);
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void clear() {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cannot clear cart with status: " + status);
        }
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }

    public void abandon() {
        if (!status.canTransitionTo(CartStatus.ABANDONED)) {
            throw new IllegalStateException("Cannot abandon cart with status: " + status);
        }
        this.status = CartStatus.ABANDONED;
        this.updatedAt = LocalDateTime.now();
    }

    public void convertToOrder() {
        if (!status.canTransitionTo(CartStatus.CONVERTED)) {
            throw new IllegalStateException("Cannot convert cart with status: " + status);
        }
        this.status = CartStatus.CONVERTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        if (!status.canTransitionTo(CartStatus.EXPIRED)) {
            throw new IllegalStateException("Cannot expire cart with status: " + status);
        }
        this.status = CartStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == CartStatus.ACTIVE;
    }

    public Money getTotalAmount() {
        if (items.isEmpty()) {
            return Money.zero(java.util.Currency.getInstance("USD"));
        }
        Money firstItemPrice = items.get(0).getUnitPrice();
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(Money.zero(firstItemPrice.getCurrency()), Money::add);
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.size();
    }

    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(item -> item.getQuantity().getValue())
                .sum();
    }

    private Optional<CartItem> findItemByProductId(ID productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }
}
