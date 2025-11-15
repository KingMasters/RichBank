package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class RemoveProductFromCartCommand {
    String customerId;
    String productId;
}

