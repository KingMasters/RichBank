package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class LoginCommand {
    String email;
    String password;
}

