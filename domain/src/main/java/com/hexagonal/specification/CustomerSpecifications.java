package com.hexagonal.specification;

import com.hexagonal.entity.Customer;
import com.hexagonal.vo.Email;

public class CustomerSpecifications {
    
    public static Specification<Customer> isActive() {
        return Customer::isActive;
    }

    public static Specification<Customer> hasEmail(Email email) {
        return customer -> customer.getEmail().equals(email);
    }

    public static Specification<Customer> nameContains(String searchTerm) {
        return customer -> customer.getFullName().toLowerCase().contains(searchTerm.toLowerCase());
    }

    public static Specification<Customer> hasAddress() {
        return customer -> customer.getAddress() != null;
    }
}

