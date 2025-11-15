package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class RegisterAccountCommand {
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}

