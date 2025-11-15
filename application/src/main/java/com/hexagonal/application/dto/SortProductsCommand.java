package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class SortProductsCommand {
    String sortBy; // "price", "name", "popularity"
    String sortOrder; // "asc", "desc"
}

