package com.hexagonal.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartStatus {
    ACTIVE("Active"),
    ABANDONED("Abandoned"),
    CONVERTED("Converted to Order"),
    EXPIRED("Expired");

    private final String description;

    public boolean canTransitionTo(CartStatus newStatus) {
        return switch (this) {
            case ACTIVE -> newStatus == ABANDONED || newStatus == CONVERTED || newStatus == EXPIRED;
            case ABANDONED, CONVERTED, EXPIRED -> false;
        };
    }
}

