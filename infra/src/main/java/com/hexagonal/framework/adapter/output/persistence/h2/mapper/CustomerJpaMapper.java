package com.hexagonal.framework.adapter.output.persistence.h2.mapper;

import com.hexagonal.domain.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.AddressEmbeddable;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.CustomerJpaEntity;
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;

public class CustomerJpaMapper {
    
    public static CustomerJpaEntity toEntity(Customer customer) {
        CustomerJpaEntity.CustomerJpaEntityBuilder builder = CustomerJpaEntity.builder()
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
    
    public static Customer toDomain(CustomerJpaEntity entity) {
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

