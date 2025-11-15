package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class FilterProductsByCategoryCommand {
    String categoryId;
}

