package com.hexagonal.application.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value
public class AddProductToCartCommand {
    String customerId;
    String productId;
    Integer quantity;
    BigDecimal unitPrice;
    Currency currency;
}

