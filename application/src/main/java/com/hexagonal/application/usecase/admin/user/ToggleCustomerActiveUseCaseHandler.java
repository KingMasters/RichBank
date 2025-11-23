package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.in.admin.user.ToggleCustomerActiveUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ToggleCustomerActiveUseCaseHandler implements ToggleCustomerActiveUseCase {
    private final CustomerRepositoryPort customerRepository;

    public ToggleCustomerActiveUseCaseHandler(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer execute(ID customerId, boolean enable) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        if (enable) {
            customer.activate();
        } else {
            customer.deactivate();
        }

        return customerRepository.save(customer);
    }
}

