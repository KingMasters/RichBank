package com.hexagonal.framework.adapter.output.persistence.h2.mapper;

import com.hexagonal.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.AddressEmbeddable;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.CustomerEntity;
import com.hexagonal.vo.Address;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;

public class CustomerEntityMapper {
    
    public static CustomerEntity toEntity(Customer customer) {
        CustomerEntity.CustomerEntityBuilder builder = CustomerEntity.builder()
            .id(customer.getId().getValue())
            .firstName(customer.getFirstName())
            .lastName(customer.getLastName())
            .email(customer.getEmail().getValue())
            .phoneNumber(customer.getPhoneNumber())
            .active(customer.isActive())
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt());
        
        if (customer.getAddress() != null) {
            Address addr = customer.getAddress();
            builder.address(AddressEmbeddable.builder()
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .zipCode(addr.getZipCode())
                .country(addr.getCountry())
                .build());
        }
        
        return builder.build();
    }
    
    public static Customer toDomain(CustomerEntity entity) {
        Customer customer = Customer.of(
            ID.of(entity.getId()),
            entity.getFirstName(),
            entity.getLastName(),
            Email.of(entity.getEmail())
        );
        
        if (entity.getPhoneNumber() != null) {
            customer.updatePhoneNumber(entity.getPhoneNumber());
        }
        
        if (entity.getAddress() != null) {
            AddressEmbeddable addr = entity.getAddress();
            customer.updateAddress(Address.of(
                addr.getStreet(),
                addr.getCity(),
                addr.getState(),
                addr.getZipCode(),
                addr.getCountry()
            ));
        }
        
        if (!entity.getActive()) {
            customer.deactivate();
        }
        
        return customer;
    }
}

