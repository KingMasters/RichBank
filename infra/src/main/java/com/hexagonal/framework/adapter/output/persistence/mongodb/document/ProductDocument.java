package com.hexagonal.framework.adapter.output.persistence.mongodb.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    private UUID id;
    
    private String name;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private String sku;
    private Integer stockQuantity;
    private String status;
    @Builder.Default
    private Set<UUID> categoryIds = new HashSet<>();
    @Builder.Default
    private List<String> images = new ArrayList<>();
    private BigDecimal weightValue;
    private String weightUnit;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

