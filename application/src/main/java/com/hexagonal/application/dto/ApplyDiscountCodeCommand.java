package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class ApplyDiscountCodeCommand {
    String customerId;
    String discountCode;
}

