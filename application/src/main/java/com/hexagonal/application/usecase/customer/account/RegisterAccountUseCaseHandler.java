package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.port.in.customer.account.RegisterAccountUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.DuplicateEntityException;
import com.hexagonal.vo.Email;

public class RegisterAccountUseCaseHandler implements RegisterAccountUseCase {
    private final CustomerRepositoryPort customerRepository;

    public RegisterAccountUseCaseHandler(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer execute(RegisterAccountCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("RegisterAccountCommand cannot be null");
        }

        Email email = Email.of(command.getEmail());

        // Check if customer with email already exists
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEntityException("Customer", "email", command.getEmail());
        }

        // Create new customer
        Customer customer = Customer.create(
                command.getFirstName(),
                command.getLastName(),
                email
        );

        // Set phone number if provided
        if (command.getPhoneNumber() != null && !command.getPhoneNumber().isBlank()) {
            customer.updatePhoneNumber(command.getPhoneNumber());
        }

        return customerRepository.save(customer);
    }
}

