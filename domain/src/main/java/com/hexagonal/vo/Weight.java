package com.hexagonal.vo;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Weight {
    BigDecimal value;
    WeightUnit unit;

    private Weight(BigDecimal value, WeightUnit unit) {
        if (value == null) {
            throw new IllegalArgumentException("Weight value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Weight unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public static Weight of(BigDecimal value, WeightUnit unit) {
        return new Weight(value, unit);
    }

    public static Weight of(double value, WeightUnit unit) {
        return new Weight(BigDecimal.valueOf(value), unit);
    }

    public static Weight zero(WeightUnit unit) {
        return new Weight(BigDecimal.ZERO, unit);
    }

    public Weight convertTo(WeightUnit targetUnit) {
        if (this.unit == targetUnit) {
            return this;
        }
        BigDecimal convertedValue = unit.convertTo(value, targetUnit);
        return new Weight(convertedValue, targetUnit);
    }

    public boolean isGreaterThan(Weight other) {
        Weight thisInOtherUnit = this.unit == other.unit ? this : this.convertTo(other.unit);
        return thisInOtherUnit.value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Weight other) {
        Weight thisInOtherUnit = this.unit == other.unit ? this : this.convertTo(other.unit);
        return thisInOtherUnit.value.compareTo(other.value) < 0;
    }

    public enum WeightUnit {
        KILOGRAM("kg"),
        GRAM("g"),
        POUND("lb"),
        OUNCE("oz");

        private final String symbol;

        WeightUnit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public BigDecimal convertTo(BigDecimal value, WeightUnit targetUnit) {
            if (this == targetUnit) {
                return value;
            }
            // Convert to kilograms first, then to target unit
            BigDecimal inKilograms = toKilograms(value);
            return fromKilograms(inKilograms, targetUnit);
        }

        private BigDecimal toKilograms(BigDecimal value) {
            return switch (this) {
                case KILOGRAM -> value;
                case GRAM -> value.divide(BigDecimal.valueOf(1000), 10, java.math.RoundingMode.HALF_UP);
                case POUND -> value.multiply(BigDecimal.valueOf(0.453592));
                case OUNCE -> value.multiply(BigDecimal.valueOf(0.0283495));
            };
        }

        private BigDecimal fromKilograms(BigDecimal kilograms, WeightUnit targetUnit) {
            return switch (targetUnit) {
                case KILOGRAM -> kilograms;
                case GRAM -> kilograms.multiply(BigDecimal.valueOf(1000));
                case POUND -> kilograms.divide(BigDecimal.valueOf(0.453592), 10, java.math.RoundingMode.HALF_UP);
                case OUNCE -> kilograms.divide(BigDecimal.valueOf(0.0283495), 10, java.math.RoundingMode.HALF_UP);
            };
        }
    }
}

