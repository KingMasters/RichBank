package com.hexagonal.application.ports.input.admin.user;

import com.hexagonal.application.usecases.admin.user.ViewCustomersUseCase;
import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;

import java.util.List;

public class ViewCustomersInputPort implements ViewCustomersUseCase {
    private final CustomerRepositoryOutputPort customerRepository;

    public ViewCustomersInputPort(CustomerRepositoryOutputPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> execute() {
        return customerRepository.findAll();
    }
}

