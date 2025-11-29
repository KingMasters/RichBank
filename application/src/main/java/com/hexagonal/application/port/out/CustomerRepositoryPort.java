package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(ID id);
    Optional<Customer> findByEmail(Email email);
    List<Customer> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
    // Retrieves the recent password hashes for a customer (most recent first).
    List<String> getPasswordHistory(ID id);

    // Updates the customer's current password and append it to history.
    void updatePassword(ID id, String hashedPassword);
}
