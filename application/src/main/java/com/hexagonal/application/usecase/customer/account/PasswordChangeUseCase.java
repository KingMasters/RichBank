package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.ChangePasswordCommand;
import com.hexagonal.application.port.out.CustomerRepositoryOutputPort;
import com.hexagonal.application.port.in.customer.account.ChangePasswordInputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class PasswordChangeUseCase implements ChangePasswordInputPort {
    private final CustomerRepositoryOutputPort customerRepository;
    private static final int PASSWORD_HISTORY_CHECK = 6;

    public PasswordChangeUseCase(CustomerRepositoryOutputPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("ChangePasswordCommand cannot be null");
        }

        ID id = ID.of(command.getCustomerId());

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + command.getCustomerId() + " not found"));

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is not active");
        }

        String currentPasswordHash = hash(command.getCurrentPassword());
        List<String> history = customerRepository.getPasswordHistory(id);

        // Verify current password matches the latest password
        if (history == null || history.isEmpty() || !history.get(0).equals(currentPasswordHash)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String newPasswordHash = hash(command.getNewPassword());

        // Check against last N passwords
        int checkCount = Math.min(PASSWORD_HISTORY_CHECK, history.size());
        for (int i = 0; i < checkCount; i++) {
            if (history.get(i).equals(newPasswordHash)) {
                throw new IllegalStateException("New password must be different from the last " + PASSWORD_HISTORY_CHECK + " passwords");
            }
        }

        // All good, update password
        customerRepository.updatePassword(id, newPasswordHash);
    }

    private String hash(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to hash password", e);
        }
    }
}

