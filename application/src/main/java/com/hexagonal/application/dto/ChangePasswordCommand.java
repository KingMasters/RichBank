package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class ChangePasswordCommand {
    String customerId;
    String currentPassword;
    String newPassword;
}

