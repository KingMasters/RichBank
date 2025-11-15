package com.hexagonal.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class ID {
    UUID value;

    private ID(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.value = value;
    }

    public static ID of(UUID value) {
        return new ID(value);
    }

    public static ID generate() {
        return new ID(UUID.randomUUID());
    }

    public static ID of(String value) {
        return new ID(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
