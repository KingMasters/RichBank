package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class CreateCategoryCommand {
    String name;
    String description;
    String parentCategoryId; // optional
}