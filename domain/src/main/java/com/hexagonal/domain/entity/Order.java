package com.hexagonal.domain.entity;

import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.OrderStatus;
import com.hexagonal.domain.vo.TrackingInformation;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Order {
    private final ID id;
    private final ID customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Money subtotalAmount;
    private Money taxAmount;
    private Money discountAmount;
    private Money totalAmount;
    private Address shippingAddress;
    private Address billingAddress;
    private TrackingInformation trackingInformation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(ID id, ID customerId, List<OrderItem> items, Address shippingAddress) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }

        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.subtotalAmount = calculateSubtotal();
        this.taxAmount = Money.zero(subtotalAmount.getCurrency());
        this.discountAmount = Money.zero(subtotalAmount.getCurrency());
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Order create(ID customerId, List<OrderItem> items, Address shippingAddress) {
        return new Order(ID.generate(), customerId, items, shippingAddress);
    }

    public static Order create(ID customerId, List<OrderItem> items, Address shippingAddress, Address billingAddress) {
        Order order = new Order(ID.generate(), customerId, items, shippingAddress);
        order.billingAddress = billingAddress;
        return order;
    }

    public static Order of(ID id, ID customerId, List<OrderItem> items, Address shippingAddress, OrderStatus status) {
        Order order = new Order(id, customerId, items, shippingAddress);
        order.status = status;
        return order;
    }

    public static Order of(ID id, ID customerId, List<OrderItem> items, Address shippingAddress, Address billingAddress,
                          Money taxAmount, Money discountAmount, OrderStatus status, TrackingInformation trackingInformation) {
        Order order = new Order(id, customerId, items, shippingAddress);
        order.billingAddress = billingAddress;
        order.taxAmount = taxAmount != null ? taxAmount : Money.zero(order.subtotalAmount.getCurrency());
        order.discountAmount = discountAmount != null ? discountAmount : Money.zero(order.subtotalAmount.getCurrency());
        order.totalAmount = order.calculateTotalAmount();
        order.status = status;
        order.trackingInformation = trackingInformation;
        return order;
    }

    public void confirm() {
        if (!status.canTransitionTo(OrderStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot confirm order with status: " + status);
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void startProcessing() {
        if (!status.canTransitionTo(OrderStatus.PROCESSING)) {
            throw new IllegalStateException("Cannot start processing order with status: " + status);
        }
        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void ship() {
        if (!status.canTransitionTo(OrderStatus.SHIPPED)) {
            throw new IllegalStateException("Cannot ship order with status: " + status);
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }

    public void ship(TrackingInformation trackingInformation) {
        if (trackingInformation == null) {
            throw new IllegalArgumentException("Tracking information cannot be null");
        }
        ship();
        this.trackingInformation = trackingInformation;
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (!status.canTransitionTo(OrderStatus.DELIVERED)) {
            throw new IllegalStateException("Cannot deliver order with status: " + status);
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!status.canTransitionTo(OrderStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot cancel order with status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateShippingAddress(Address newAddress) {
        if (newAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot update shipping address for order with status: " + status);
        }
        this.shippingAddress = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBillingAddress(Address newAddress) {
        if (newAddress == null) {
            throw new IllegalArgumentException("Billing address cannot be null");
        }
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot update billing address for order with status: " + status);
        }
        this.billingAddress = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBillingAddress(Address billingAddress) {
        if (billingAddress == null) {
            throw new IllegalArgumentException("Billing address cannot be null");
        }
        this.billingAddress = billingAddress;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTaxAmount(Money taxAmount) {
        if (taxAmount == null) {
            throw new IllegalArgumentException("Tax amount cannot be null");
        }
        if (taxAmount.isNegative()) {
            throw new IllegalArgumentException("Tax amount cannot be negative");
        }
        if (!taxAmount.getCurrency().equals(subtotalAmount.getCurrency())) {
            throw new IllegalArgumentException("Tax amount currency must match subtotal currency");
        }
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot update tax amount for order with status: " + status);
        }
        this.taxAmount = taxAmount;
        this.totalAmount = calculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
    }

    public void setDiscountAmount(Money discountAmount) {
        if (discountAmount == null) {
            throw new IllegalArgumentException("Discount amount cannot be null");
        }
        if (discountAmount.isNegative()) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
        if (!discountAmount.getCurrency().equals(subtotalAmount.getCurrency())) {
            throw new IllegalArgumentException("Discount amount currency must match subtotal currency");
        }
        if (discountAmount.isGreaterThan(subtotalAmount)) {
            throw new IllegalArgumentException("Discount amount cannot be greater than subtotal amount");
        }
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot update discount amount for order with status: " + status);
        }
        this.discountAmount = discountAmount;
        this.totalAmount = calculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTrackingInformation(TrackingInformation trackingInformation) {
        if (trackingInformation == null) {
            throw new IllegalArgumentException("Tracking information cannot be null");
        }
        if (status != OrderStatus.SHIPPED && status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Can only update tracking information for shipped or delivered orders");
        }
        this.trackingInformation = trackingInformation;
        this.updatedAt = LocalDateTime.now();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private Money calculateSubtotal() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.zero(items.get(0).getUnitPrice().getCurrency()), Money::add);
    }

    private Money calculateTotalAmount() {
        Money subtotal = subtotalAmount != null ? subtotalAmount : calculateSubtotal();
        Money tax = taxAmount != null ? taxAmount : Money.zero(subtotal.getCurrency());
        Money discount = discountAmount != null ? discountAmount : Money.zero(subtotal.getCurrency());
        
        // Total = Subtotal + Tax - Discount
        return subtotal.add(tax).subtract(discount);
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED || status == OrderStatus.PROCESSING;
    }

    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED;
    }
}
