package com.hexagonal.domain.entity;

import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Customer {
    private final ID id;
    private String firstName;
    private String lastName;
    private Email email;
    private String phoneNumber;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private Customer(ID id, String firstName, String lastName, Email email) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Customer create(String firstName, String lastName, Email email) {
        return new Customer(ID.generate(), firstName, lastName, email);
    }

    public static Customer of(ID id, String firstName, String lastName, Email email) {
        return new Customer(id, firstName, lastName, email);
    }

    public void updatePersonalInfo(String firstName, String lastName) {
        if (firstName != null && !firstName.isBlank()) {
            this.firstName = firstName.trim();
        }
        if (lastName != null && !lastName.isBlank()) {
            this.lastName = lastName.trim();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAddress(Address address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
