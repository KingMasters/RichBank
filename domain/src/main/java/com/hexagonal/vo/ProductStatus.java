package com.hexagonal.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    ACTIVE("Active"),
    DISCONTINUED("Discontinued"),
    OUT_OF_STOCK("Out of Stock");

    private final String description;

    public boolean canTransitionTo(ProductStatus newStatus) {
        return switch (this) {
            case ACTIVE -> newStatus == DISCONTINUED || newStatus == OUT_OF_STOCK;
            case OUT_OF_STOCK -> newStatus == ACTIVE || newStatus == DISCONTINUED;
            case DISCONTINUED -> false; // Discontinued products cannot change status
        };
    }
}

