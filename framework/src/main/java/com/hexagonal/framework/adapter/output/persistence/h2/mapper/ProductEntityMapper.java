package com.hexagonal.framework.adapter.output.persistence.h2.mapper;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.ProductEntity;
import com.hexagonal.domain.vo.Dimensions;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.ProductStatus;
import com.hexagonal.domain.vo.Quantity;
import com.hexagonal.domain.vo.Weight;

import java.util.Currency;
import java.util.stream.Collectors;

public class ProductEntityMapper {
    
    public static ProductEntity toEntity(Product product) {
        ProductEntity.ProductEntityBuilder builder = ProductEntity.builder()
            .id(product.getId().getValue())
            .name(product.getName())
            .description(product.getDescription())
            .priceAmount(product.getPrice().getAmount())
            .priceCurrency(product.getPrice().getCurrency().getCurrencyCode())
            .sku(product.getSku())
            .stockQuantity(product.getStockQuantity().getValue())
            .status(product.getStatus().name())
            .categoryIds(product.getCategoryIds().stream()
                .map(ID::getValue)
                .collect(Collectors.toSet()))
            .images(product.getImages())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt());
        
        if (product.getWeight() != null) {
            builder.weightValue(product.getWeight().getValue())
                   .weightUnit(product.getWeight().getUnit().name());
        }
        
        if (product.getDimensions() != null) {
            builder.length(product.getDimensions().getLength())
                   .width(product.getDimensions().getWidth())
                   .height(product.getDimensions().getHeight())
                   .dimensionUnit(product.getDimensions().getUnit().name());
        }
        
        return builder.build();
    }
    
    public static Product toDomain(ProductEntity entity) {
        Money price = Money.of(entity.getPriceAmount(), Currency.getInstance(entity.getPriceCurrency()));
        Product product = Product.of(
            ID.of(entity.getId()),
            entity.getName(),
            price,
            entity.getSku(),
            ProductStatus.valueOf(entity.getStatus())
        );
        
        if (entity.getDescription() != null) {
            product.updateDescription(entity.getDescription());
        }
        
        product.setStock(Quantity.of(entity.getStockQuantity()));
        
        if (entity.getCategoryIds() != null && !entity.getCategoryIds().isEmpty()) {
            entity.getCategoryIds().forEach(categoryId -> 
                product.addCategory(ID.of(categoryId))
            );
        }
        
        if (entity.getImages() != null) {
            entity.getImages().forEach(product::addImage);
        }
        
        if (entity.getWeightValue() != null && entity.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(entity.getWeightUnit());
            product.setWeight(Weight.of(entity.getWeightValue(), weightUnit));
        }
        
        if (entity.getLength() != null && entity.getWidth() != null && 
            entity.getHeight() != null && entity.getDimensionUnit() != null) {
            Dimensions.LengthUnit dimensionUnit = Dimensions.LengthUnit.valueOf(entity.getDimensionUnit());
            product.setDimensions(Dimensions.of(
                entity.getLength(),
                entity.getWidth(),
                entity.getHeight(),
                dimensionUnit
            ));
        }
        
        return product;
    }
}

