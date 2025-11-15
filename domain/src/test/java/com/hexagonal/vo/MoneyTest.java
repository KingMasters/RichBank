package com.hexagonal.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    @DisplayName("Should create money with valid amount and currency")
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        // Given
        BigDecimal amount = new BigDecimal("99.99");

        // When
        Money money = Money.of(amount, USD);

        // Then
        assertThat(money.getAmount()).isEqualByComparingTo(amount);
        assertThat(money.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        // Then
        assertThatThrownBy(() -> Money.of(null, USD))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Amount cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when currency is null")
    void shouldThrowExceptionWhenCurrencyIsNull() {
        // Then
        BigDecimal amount = new BigDecimal("100");
        assertThatThrownBy(() -> Money.of(amount, (Currency) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency cannot be null");
    }

    @Test
    @DisplayName("Should create zero money")
    void shouldCreateZeroMoney() {
        // When
        Money zero = Money.zero(USD);

        // Then
        assertThat(zero.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(zero.getCurrency()).isEqualTo(USD);
        assertThat(zero.isZero()).isTrue();
    }

    @Test
    @DisplayName("Should add two money amounts with same currency")
    void shouldAddTwoMoneyAmountsWithSameCurrency() {
        // Given
        Money money1 = Money.of(new BigDecimal("50.00"), USD);
        Money money2 = Money.of(new BigDecimal("25.50"), USD);

        // When
        Money result = money1.add(money2);

        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("75.50"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should throw exception when adding money with different currencies")
    void shouldThrowExceptionWhenAddingMoneyWithDifferentCurrencies() {
        // Given
        Money money1 = Money.of(new BigDecimal("50.00"), USD);
        Money money2 = Money.of(new BigDecimal("25.50"), EUR);

        // Then
        assertThatThrownBy(() -> money1.add(money2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot add money with different currencies");
    }

    @Test
    @DisplayName("Should subtract two money amounts with same currency")
    void shouldSubtractTwoMoneyAmountsWithSameCurrency() {
        // Given
        Money money1 = Money.of(new BigDecimal("50.00"), USD);
        Money money2 = Money.of(new BigDecimal("25.50"), USD);

        // When
        Money result = money1.subtract(money2);

        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("24.50"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should multiply money by multiplier")
    void shouldMultiplyMoneyByMultiplier() {
        // Given
        Money money = Money.of(new BigDecimal("10.00"), USD);
        BigDecimal multiplier = new BigDecimal("2");

        // When
        Money result = money.multiply(multiplier);

        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should check if money is greater than")
    void shouldCheckIfMoneyIsGreaterThan() {
        // Given
        Money money1 = Money.of(new BigDecimal("50.00"), USD);
        Money money2 = Money.of(new BigDecimal("25.00"), USD);

        // When & Then
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isGreaterThan(money1)).isFalse();
    }

    @Test
    @DisplayName("Should check if money is positive")
    void shouldCheckIfMoneyIsPositive() {
        // Given
        Money positive = Money.of(new BigDecimal("10.00"), USD);
        Money zero = Money.zero(USD);

        // When & Then
        assertThat(positive.isPositive()).isTrue();
        assertThat(zero.isPositive()).isFalse();
    }

    @Test
    @DisplayName("Should check if money is negative")
    void shouldCheckIfMoneyIsNegative() {
        // Given
        Money positive = Money.of(new BigDecimal("10.00"), USD);
        Money zero = Money.zero(USD);

        // When & Then
        assertThat(positive.isNegative()).isFalse();
        assertThat(zero.isNegative()).isFalse();
    }
}

