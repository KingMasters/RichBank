package com.hexagonal.application.ports.input.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginInputPort Tests")
class LoginInputPortTest {

    @Mock
    private CustomerRepositoryOutputPort customerRepository;

    @InjectMocks
    private LoginInputPort loginInputPort;

    private LoginCommand command;
    private Customer customer;

    @BeforeEach
    void setUp() {
        command = new LoginCommand("john.doe@example.com", "password123");
        customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Given
        when(customerRepository.findByEmail(Email.of(command.getEmail())))
            .thenReturn(Optional.of(customer));

        // When
        Customer result = loginInputPort.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail().getValue()).isEqualTo(command.getEmail());
        verify(customerRepository).findByEmail(Email.of(command.getEmail()));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findByEmail(Email.of(command.getEmail())))
            .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginInputPort.execute(command))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Customer with email")
            .hasMessageContaining(command.getEmail());

        verify(customerRepository).findByEmail(Email.of(command.getEmail()));
    }

    @Test
    @DisplayName("Should throw exception when customer is inactive")
    void shouldThrowExceptionWhenCustomerIsInactive() {
        // Given
        customer.deactivate();
        when(customerRepository.findByEmail(Email.of(command.getEmail())))
            .thenReturn(Optional.of(customer));

        // Then
        assertThatThrownBy(() -> loginInputPort.execute(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Customer account is not active");

        verify(customerRepository).findByEmail(Email.of(command.getEmail()));
    }

    @Test
    @DisplayName("Should throw exception when command is null")
    void shouldThrowExceptionWhenCommandIsNull() {
        // Then
        assertThatThrownBy(() -> loginInputPort.execute(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("LoginCommand cannot be null");
    }
}

