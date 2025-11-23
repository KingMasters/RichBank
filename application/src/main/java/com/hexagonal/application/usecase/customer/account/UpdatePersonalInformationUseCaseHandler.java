package com.hexagonal.application.usecase.customer.account;

import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.application.port.in.customer.account.UpdatePersonalInformationUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;

public class UpdatePersonalInformationUseCaseHandler implements UpdatePersonalInformationUseCase {
    private final CustomerRepositoryPort customerRepository;

    public UpdatePersonalInformationUseCaseHandler(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer execute(UpdatePersonalInformationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("UpdatePersonalInformationCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Update name if provided
        if (command.getFirstName() != null || command.getLastName() != null) {
            String firstName = command.getFirstName() != null ? command.getFirstName() : customer.getFirstName();
            String lastName = command.getLastName() != null ? command.getLastName() : customer.getLastName();
            customer.updatePersonalInfo(firstName, lastName);
        }

        // Update email if provided
        if (command.getEmail() != null && !command.getEmail().isBlank()) {
            Email email = Email.of(command.getEmail());
            customer.updateEmail(email);
        }

        // Update phone number if provided
        if (command.getPhoneNumber() != null) {
            customer.updatePhoneNumber(command.getPhoneNumber());
        }

        return customerRepository.save(customer);
    }
}

