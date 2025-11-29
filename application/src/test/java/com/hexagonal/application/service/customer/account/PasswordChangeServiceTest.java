package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.dto.ChangePasswordCommand;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.application.service.command.customer.account.PasswordChangeService;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordChangeInputPort Tests")
class PasswordChangeServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @InjectMocks
    private PasswordChangeService passwordChangeService;

    private Customer customer;
    private ID id;

    @BeforeEach
    void setUp() {
        customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
        id = ID.of(customer.getId().getValue());
    }

    private String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashed) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Test
    @DisplayName("Should change password when current password matches and new password is not in last 6")
    void shouldChangePasswordSuccessfully() throws Exception {
        // Given
        String current = "currentPass";
        String newPass = "newPass";
        String currentHash = sha256(current);
        String newHash = sha256(newPass);
        List<String> history = new ArrayList<>(Arrays.asList(currentHash));

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.getPasswordHistory(id)).thenReturn(history);

        ChangePasswordCommand command = new ChangePasswordCommand(customer.getId().getValue().toString(), current, newPass);

        // When
        passwordChangeService.execute(command);

        // Then
        verify(customerRepository).updatePassword(eq(id), eq(newHash));
    }

    @Test
    @DisplayName("Should throw when new password is among last 6 passwords")
    void shouldThrowWhenNewPasswordInHistory() throws Exception {
        // Given
        String current = "currentPass";
        String newPass = "oldPass";
        String currentHash = sha256(current);
        String newHash = sha256(newPass);
        // history contains newHash within last 6
        List<String> history = Arrays.asList(currentHash, newHash);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.getPasswordHistory(id)).thenReturn(history);

        ChangePasswordCommand command = new ChangePasswordCommand(customer.getId().getValue().toString(), current, newPass);

        // Then
        assertThatThrownBy(() -> passwordChangeService.execute(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("New password must be different");

        verify(customerRepository, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("Should throw when current password is incorrect")
    void shouldThrowWhenCurrentPasswordIncorrect() throws Exception {
        // Given
        String current = "wrongCurrent";
        String actualCurrent = "actualCurrent";
        String actualHash = sha256(actualCurrent);
        List<String> history = Arrays.asList(actualHash);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.getPasswordHistory(id)).thenReturn(history);

        ChangePasswordCommand command = new ChangePasswordCommand(customer.getId().getValue().toString(), current, "newPass");

        // Then
        assertThatThrownBy(() -> passwordChangeService.execute(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Current password is incorrect");

        verify(customerRepository, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("Should throw when command is null")
    void shouldThrowWhenCommandIsNull() {
        assertThatThrownBy(() -> passwordChangeService.execute(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ChangePasswordCommand cannot be null");
    }
}

