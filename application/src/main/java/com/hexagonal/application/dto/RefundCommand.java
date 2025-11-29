package com.hexagonal.application.dto;

import lombok.Value;

import com.hexagonal.domain.vo.Money;

@Value
public class RefundCommand {
    Money amount;
    String reason;
    String paymentId; // optional, can be null
}
