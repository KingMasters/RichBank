package com.hexagonal.application.ports.input.admin.user;

import com.hexagonal.application.usecases.admin.user.ToggleCustomerActiveUseCase;
import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ToggleCustomerActiveInputPort implements ToggleCustomerActiveUseCase {
    private final CustomerRepositoryOutputPort customerRepository;

    public ToggleCustomerActiveInputPort(CustomerRepositoryOutputPort customerRepository) {
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

