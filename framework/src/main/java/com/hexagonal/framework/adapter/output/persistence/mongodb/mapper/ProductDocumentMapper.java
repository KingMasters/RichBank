package com.hexagonal.framework.adapter.output.persistence.mongodb.mapper;

import com.hexagonal.entity.Product;
import com.hexagonal.framework.adapter.output.persistence.mongodb.document.ProductDocument;
import com.hexagonal.vo.Dimensions;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.ProductStatus;
import com.hexagonal.vo.Quantity;
import com.hexagonal.vo.Weight;

import java.util.Currency;
import java.util.stream.Collectors;

public class ProductDocumentMapper {
    
    public static ProductDocument toDocument(Product product) {
        ProductDocument.ProductDocumentBuilder builder = ProductDocument.builder()
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
    
    public static Product toDomain(ProductDocument document) {
        Money price = Money.of(document.getPriceAmount(), Currency.getInstance(document.getPriceCurrency()));
        Product product = Product.of(
            ID.of(document.getId()),
            document.getName(),
            price,
            document.getSku(),
            ProductStatus.valueOf(document.getStatus())
        );
        
        if (document.getDescription() != null) {
            product.updateDescription(document.getDescription());
        }
        
        product.setStock(Quantity.of(document.getStockQuantity()));
        
        if (document.getCategoryIds() != null && !document.getCategoryIds().isEmpty()) {
            document.getCategoryIds().forEach(categoryId -> 
                product.addCategory(ID.of(categoryId))
            );
        }
        
        if (document.getImages() != null) {
            document.getImages().forEach(product::addImage);
        }
        
        if (document.getWeightValue() != null && document.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(document.getWeightUnit());
            product.setWeight(Weight.of(document.getWeightValue(), weightUnit));
        }
        
        if (document.getLength() != null && document.getWidth() != null && 
            document.getHeight() != null && document.getDimensionUnit() != null) {
            Dimensions.LengthUnit dimensionUnit = Dimensions.LengthUnit.valueOf(document.getDimensionUnit());
            product.setDimensions(Dimensions.of(
                document.getLength(),
                document.getWidth(),
                document.getHeight(),
                dimensionUnit
            ));
        }
        
        return product;
    }
}

