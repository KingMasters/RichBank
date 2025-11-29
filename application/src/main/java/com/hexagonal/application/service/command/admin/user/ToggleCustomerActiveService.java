package com.hexagonal.application.service.command.admin.user;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.user.ToggleCustomerActiveUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ToggleCustomerActiveService implements ToggleCustomerActiveUseCase {
    private final CustomerRepositoryPort customerRepository;

    public ToggleCustomerActiveService(CustomerRepositoryPort customerRepository) {
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

