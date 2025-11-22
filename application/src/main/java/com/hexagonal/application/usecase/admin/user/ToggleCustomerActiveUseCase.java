package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.in.admin.user.ToggleCustomerActiveInputPort;
import com.hexagonal.application.port.out.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ToggleCustomerActiveUseCase implements ToggleCustomerActiveInputPort {
    private final CustomerRepositoryOutputPort customerRepository;

    public ToggleCustomerActiveUseCase(CustomerRepositoryOutputPort customerRepository) {
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

