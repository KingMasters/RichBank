package com.hexagonal.entity;

import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Payment {
    private final ID id;
    private final ID orderId;
    private final Money amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Payment(ID id, ID orderId, Money amount, String paymentMethod) {
        if (id == null) {
            throw new IllegalArgumentException("Payment ID cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (amount == null || amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod.trim();
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Payment create(ID orderId, Money amount, String paymentMethod) {
        return new Payment(ID.generate(), orderId, amount, paymentMethod);
    }

    public static Payment of(ID id, ID orderId, Money amount, String paymentMethod, PaymentStatus status, String transactionId) {
        Payment payment = new Payment(id, orderId, amount, paymentMethod);
        payment.status = status;
        payment.transactionId = transactionId;
        return payment;
    }

    public void startProcessing() {
        if (!status.canTransitionTo(PaymentStatus.PROCESSING)) {
            throw new IllegalStateException("Cannot start processing payment with status: " + status);
        }
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(String transactionId) {
        if (!status.canTransitionTo(PaymentStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot complete payment with status: " + status);
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (!status.canTransitionTo(PaymentStatus.FAILED)) {
            throw new IllegalStateException("Cannot fail payment with status: " + status);
        }
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void refund() {
        if (!status.canTransitionTo(PaymentStatus.REFUNDED)) {
            throw new IllegalStateException("Cannot refund payment with status: " + status);
        }
        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED;
    }
}
