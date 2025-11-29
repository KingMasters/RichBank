package com.hexagonal.entity;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Customer Entity Tests")
class CustomerTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final Email EMAIL = Email.of("john.doe@example.com");

    @Test
    @DisplayName("Should create customer with valid data")
    void shouldCreateCustomerWithValidData() {
        // When
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(customer.getLastName()).isEqualTo(LAST_NAME);
        assertThat(customer.getEmail()).isEqualTo(EMAIL);
        assertThat(customer.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when first name is null")
    void shouldThrowExceptionWhenFirstNameIsNull() {
        // Then
        assertThatThrownBy(() -> Customer.create(null, LAST_NAME, EMAIL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when last name is null")
    void shouldThrowExceptionWhenLastNameIsNull() {
        // Then
        assertThatThrownBy(() -> Customer.create(FIRST_NAME, null, EMAIL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Last name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Then
        assertThatThrownBy(() -> Customer.create(FIRST_NAME, LAST_NAME, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null");
    }

    @Test
    @DisplayName("Should update personal information")
    void shouldUpdatePersonalInformation() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);
        String newFirstName = "Jane";
        String newLastName = "Smith";

        // When
        customer.updatePersonalInfo(newFirstName, newLastName);

        // Then
        assertThat(customer.getFirstName()).isEqualTo(newFirstName);
        assertThat(customer.getLastName()).isEqualTo(newLastName);
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);
        Email newEmail = Email.of("newemail@example.com");

        // When
        customer.updateEmail(newEmail);

        // Then
        assertThat(customer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Should update phone number")
    void shouldUpdatePhoneNumber() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);
        String phoneNumber = "+1234567890";

        // When
        customer.updatePhoneNumber(phoneNumber);

        // Then
        assertThat(customer.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    @DisplayName("Should update address")
    void shouldUpdateAddress() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);
        Address address = Address.of(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );

        // When
        customer.updateAddress(address);

        // Then
        assertThat(customer.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should deactivate customer")
    void shouldDeactivateCustomer() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);

        // When
        customer.deactivate();

        // Then
        assertThat(customer.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should activate customer")
    void shouldActivateCustomer() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);
        customer.deactivate();

        // When
        customer.activate();

        // Then
        assertThat(customer.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return full name")
    void shouldReturnFullName() {
        // Given
        Customer customer = Customer.create(FIRST_NAME, LAST_NAME, EMAIL);

        // When
        String fullName = customer.getFullName();

        // Then
        assertThat(fullName).isEqualTo(FIRST_NAME + " " + LAST_NAME);
    }
}

