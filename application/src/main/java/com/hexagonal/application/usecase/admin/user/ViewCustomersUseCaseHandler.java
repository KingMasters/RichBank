package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.in.admin.user.ViewCustomersUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.entity.Customer;

import java.util.List;

public class ViewCustomersUseCaseHandler implements ViewCustomersUseCase {
    private final CustomerRepositoryPort customerRepository;

    public ViewCustomersUseCaseHandler(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> execute() {
        return customerRepository.findAll();
    }
}

