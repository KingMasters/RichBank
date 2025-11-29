package com.hexagonal.application.service.admin.user;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.user.ViewCustomersUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;

import java.util.List;

@UseCase
public class ViewCustomersService implements ViewCustomersUseCase {
    private final CustomerRepositoryPort customerRepository;

    public ViewCustomersService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> execute() {
        return customerRepository.findAll();
    }
}

