package com.hexagonal.repository;

import com.hexagonal.entity.Customer;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(ID id);
    Optional<Customer> findByEmail(Email email);
    List<Customer> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
    boolean existsByEmail(Email email);
}

