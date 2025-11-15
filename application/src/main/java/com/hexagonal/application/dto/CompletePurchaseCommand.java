package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class CompletePurchaseCommand {
    String customerId;
    String shippingAddressId;
    String billingAddressId;
    String paymentMethod;
    String discountCode;
}

