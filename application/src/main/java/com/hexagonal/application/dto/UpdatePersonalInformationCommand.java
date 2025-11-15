package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class UpdatePersonalInformationCommand {
    String customerId;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}

