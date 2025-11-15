package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class SelectPaymentMethodCommand {
    String customerId;
    String paymentMethod; // "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", etc.
    String paymentDetails; // JSON string or other format
}

