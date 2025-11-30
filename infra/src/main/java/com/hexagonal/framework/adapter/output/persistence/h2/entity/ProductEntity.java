package com.hexagonal.framework.adapter.output.persistence.h2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;
    
    @Column(nullable = false, length = 3)
    private String priceCurrency;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(nullable = false)
    private String status;
    
    @ElementCollection
    @CollectionTable(name = "product_category_ids", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category_id", columnDefinition = "UUID")
    @Builder.Default
    private Set<UUID> categoryIds = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    private BigDecimal weightValue;
    private String weightUnit;
    
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUnit;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

