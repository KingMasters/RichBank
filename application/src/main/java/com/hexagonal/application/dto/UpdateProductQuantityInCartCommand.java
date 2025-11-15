package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class UpdateProductQuantityInCartCommand {
    String customerId;
    String productId;
    Integer quantity;
}

