package com.hexagonal.domain.vo;

import lombok.Value;

@Value
public class Quantity {
    int value;

    private Quantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public static Quantity zero() {
        return new Quantity(0);
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    public Quantity subtract(Quantity other) {
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Quantity(result);
    }

    public Quantity multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        return new Quantity(this.value * multiplier);
    }

    public boolean isGreaterThan(Quantity other) {
        return this.value > other.value;
    }

    public boolean isLessThan(Quantity other) {
        return this.value < other.value;
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isPositive() {
        return value > 0;
    }
}

