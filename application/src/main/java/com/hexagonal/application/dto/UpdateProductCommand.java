package com.hexagonal.application.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;

@Value
public class UpdateProductCommand {
    String productId;
    String name;
    String description;
    BigDecimal price;
    Currency currency;
    Set<String> categoryIds;
    List<String> images;
    BigDecimal weight;
    String weightUnit;
    BigDecimal length;
    BigDecimal width;
    BigDecimal height;
    String dimensionUnit;
}
