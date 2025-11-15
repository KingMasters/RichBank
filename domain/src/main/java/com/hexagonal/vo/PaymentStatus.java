package com.hexagonal.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    REFUNDED("Refunded");

    private final String description;

    public boolean canTransitionTo(PaymentStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PROCESSING || newStatus == FAILED;
            case PROCESSING -> newStatus == COMPLETED || newStatus == FAILED;
            case COMPLETED -> newStatus == REFUNDED;
            case FAILED, REFUNDED -> false;
        };
    }
}

