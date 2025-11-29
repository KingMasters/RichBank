package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.application.service.command.customer.account.LoginUseService;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.Email;
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
class LoginUseServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @InjectMocks
    private LoginUseService loginUseService;

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
        Customer result = loginUseService.execute(command);

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
        assertThatThrownBy(() -> loginUseService.execute(command))
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
        assertThatThrownBy(() -> loginUseService.execute(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Customer account is not active");

        verify(customerRepository).findByEmail(Email.of(command.getEmail()));
    }

    @Test
    @DisplayName("Should throw exception when command is null")
    void shouldThrowExceptionWhenCommandIsNull() {
        // Then
        assertThatThrownBy(() -> loginUseService.execute(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("LoginCommand cannot be null");
    }
}

