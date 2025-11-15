package com.hexagonal.vo;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Dimensions {
    BigDecimal length;
    BigDecimal width;
    BigDecimal height;
    LengthUnit unit;

    private Dimensions(BigDecimal length, BigDecimal width, BigDecimal height, LengthUnit unit) {
        if (length == null || length.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Length cannot be null or negative");
        }
        if (width == null || width.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Width cannot be null or negative");
        }
        if (height == null || height.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Height cannot be null or negative");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Length unit cannot be null");
        }
        this.length = length;
        this.width = width;
        this.height = height;
        this.unit = unit;
    }

    public static Dimensions of(BigDecimal length, BigDecimal width, BigDecimal height, LengthUnit unit) {
        return new Dimensions(length, width, height, unit);
    }

    public static Dimensions of(double length, double width, double height, LengthUnit unit) {
        return new Dimensions(
                BigDecimal.valueOf(length),
                BigDecimal.valueOf(width),
                BigDecimal.valueOf(height),
                unit
        );
    }

    public BigDecimal getVolume() {
        return length.multiply(width).multiply(height);
    }

    public Dimensions convertTo(LengthUnit targetUnit) {
        if (this.unit == targetUnit) {
            return this;
        }
        BigDecimal lengthInTarget = unit.convertTo(length, targetUnit);
        BigDecimal widthInTarget = unit.convertTo(width, targetUnit);
        BigDecimal heightInTarget = unit.convertTo(height, targetUnit);
        return new Dimensions(lengthInTarget, widthInTarget, heightInTarget, targetUnit);
    }

    public enum LengthUnit {
        METER("m"),
        CENTIMETER("cm"),
        MILLIMETER("mm"),
        INCH("in"),
        FOOT("ft");

        private final String symbol;

        LengthUnit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public BigDecimal convertTo(BigDecimal value, LengthUnit targetUnit) {
            if (this == targetUnit) {
                return value;
            }
            // Convert to meters first, then to target unit
            BigDecimal inMeters = toMeters(value);
            return fromMeters(inMeters, targetUnit);
        }

        private BigDecimal toMeters(BigDecimal value) {
            return switch (this) {
                case METER -> value;
                case CENTIMETER -> value.divide(BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP);
                case MILLIMETER -> value.divide(BigDecimal.valueOf(1000), 10, java.math.RoundingMode.HALF_UP);
                case INCH -> value.multiply(BigDecimal.valueOf(0.0254));
                case FOOT -> value.multiply(BigDecimal.valueOf(0.3048));
            };
        }

        private BigDecimal fromMeters(BigDecimal meters, LengthUnit targetUnit) {
            return switch (targetUnit) {
                case METER -> meters;
                case CENTIMETER -> meters.multiply(BigDecimal.valueOf(100));
                case MILLIMETER -> meters.multiply(BigDecimal.valueOf(1000));
                case INCH -> meters.divide(BigDecimal.valueOf(0.0254), 10, java.math.RoundingMode.HALF_UP);
                case FOOT -> meters.divide(BigDecimal.valueOf(0.3048), 10, java.math.RoundingMode.HALF_UP);
            };
        }
    }
}

