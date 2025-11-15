package com.hexagonal.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("Should create email with valid format")
    void shouldCreateEmailWithValidFormat() {
        // Given
        String validEmail = "test@example.com";

        // When
        Email email = Email.of(validEmail);

        // Then
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Then
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when email is empty")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Then
        assertThatThrownBy(() -> Email.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when email format is invalid")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // Then
        assertThatThrownBy(() -> Email.of("invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Should trim whitespace from email")
    void shouldTrimWhitespaceFromEmail() {
        // Given
        String emailWithWhitespace = "  test@example.com  ";

        // When
        Email email = Email.of(emailWithWhitespace);

        // Then
        assertThat(email.getValue()).isEqualTo("test@example.com");
    }
}

