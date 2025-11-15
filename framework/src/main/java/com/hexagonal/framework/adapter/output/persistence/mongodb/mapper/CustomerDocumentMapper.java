package com.hexagonal.framework.adapter.output.persistence.mongodb.mapper;

import com.hexagonal.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.mongodb.document.AddressDocument;
import com.hexagonal.framework.adapter.output.persistence.mongodb.document.CustomerDocument;
import com.hexagonal.vo.Address;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;

public class CustomerDocumentMapper {
    
    public static CustomerDocument toDocument(Customer customer) {
        CustomerDocument.CustomerDocumentBuilder builder = CustomerDocument.builder()
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
            builder.address(AddressDocument.builder()
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .zipCode(addr.getZipCode())
                .country(addr.getCountry())
                .build());
        }
        
        return builder.build();
    }
    
    public static Customer toDomain(CustomerDocument document) {
        Customer customer = Customer.of(
            ID.of(document.getId()),
            document.getFirstName(),
            document.getLastName(),
            Email.of(document.getEmail())
        );
        
        if (document.getPhoneNumber() != null) {
            customer.updatePhoneNumber(document.getPhoneNumber());
        }
        
        if (document.getAddress() != null) {
            AddressDocument addr = document.getAddress();
            customer.updateAddress(Address.of(
                addr.getStreet(),
                addr.getCity(),
                addr.getState(),
                addr.getZipCode(),
                addr.getCountry()
            ));
        }
        
        if (!document.getActive()) {
            customer.deactivate();
        }
        
        return customer;
    }
}

