package com.hexagonal.domain.service;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.Email;

/**
 * Domain Service for Customer Entity
 * Sadece domain business logic'ini handle eder, persistence ile ilgilenmez
 * Port'a bağımlı değildir
 */
public class CustomerDomainService {

    /**
     * Domain logic: Müşteri bilgilerini güncelle
     */
    public void updatePersonalInformation(Customer customer, String firstName, String lastName) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        customer.updatePersonalInfo(firstName, lastName);
    }

    /**
     * Domain logic: Müşteri adresi güncelle
     */
    public void updateAddress(Customer customer, Address address) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        customer.updateAddress(address);
    }

    /**
     * Domain logic: Müşteri telefon numarasını güncelle
     */
    public void updatePhoneNumber(Customer customer, String phoneNumber) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        customer.updatePhoneNumber(phoneNumber);
    }

    /**
     * Domain logic: Müşteri hesabını deaktif et
     */
    public void deactivateAccount(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        customer.deactivate();
    }
}

