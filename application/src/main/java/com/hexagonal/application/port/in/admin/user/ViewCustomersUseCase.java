package com.hexagonal.application.port.in.admin.user;

import com.hexagonal.entity.Customer;

import java.util.List;

public interface ViewCustomersUseCase {
    List<Customer> execute();
}

