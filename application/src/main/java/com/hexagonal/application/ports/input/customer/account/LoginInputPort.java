package com.hexagonal.application.ports.input.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.account.LoginUseCase;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Email;

public class LoginInputPort implements LoginUseCase {
    private final CustomerRepositoryOutputPort customerRepository;

    public LoginInputPort(CustomerRepositoryOutputPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer execute(LoginCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("LoginCommand cannot be null");
        }

        Email email = Email.of(command.getEmail());
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Customer with email " + command.getEmail() + " not found"));

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is not active");
        }

        // Note: Password validation would typically be handled by a separate authentication service
        // This is a simplified version for the hexagon structure

        return customer;
    }
}

