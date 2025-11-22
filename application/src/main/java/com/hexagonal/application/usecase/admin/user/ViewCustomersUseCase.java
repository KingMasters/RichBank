package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.out.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;

import java.util.List;

public class ViewCustomersUseCase implements com.hexagonal.application.port.in.admin.user.ViewCustomersInputPort {
    private final CustomerRepositoryOutputPort customerRepository;

    public ViewCustomersUseCase(CustomerRepositoryOutputPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> execute() {
        return customerRepository.findAll();
    }
}

