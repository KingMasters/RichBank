package com.hexagonal.domain.repository;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;

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

