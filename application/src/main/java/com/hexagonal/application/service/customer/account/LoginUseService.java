package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.in.customer.account.LoginUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.Email;

@UseCase
public class LoginUseService implements LoginUseCase {
    private final CustomerRepositoryPort customerRepository;

    public LoginUseService(CustomerRepositoryPort customerRepository) {
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

