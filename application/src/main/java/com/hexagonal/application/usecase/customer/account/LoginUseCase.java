package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.out.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Email;

public class LoginUseCase implements com.hexagonal.application.port.in.customer.account.LoginInputPort {
    private final CustomerRepositoryOutputPort customerRepository;

    public LoginUseCase(CustomerRepositoryOutputPort customerRepository) {
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

