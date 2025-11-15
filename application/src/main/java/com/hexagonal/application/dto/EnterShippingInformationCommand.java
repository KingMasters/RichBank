package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class EnterShippingInformationCommand {
    String customerId;
    String street;
    String city;
    String state;
    String zipCode;
    String country;
}

