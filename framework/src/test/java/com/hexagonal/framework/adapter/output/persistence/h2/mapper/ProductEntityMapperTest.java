package com.hexagonal.framework.adapter.output.persistence.h2.mapper;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.*;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProductEntityMapper Tests")
class ProductEntityMapperTest {

    private static final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("Should map product to entity")
    void shouldMapProductToEntity() {
        // Given
        Product product = Product.create(
            "Test Product",
            Money.of(new BigDecimal("99.99"), USD),
            "PROD-001"
        );
        product.updateDescription("Test Description");
        product.addStock(Quantity.of(10));
        ID categoryId = ID.generate();
        product.addCategory(categoryId);
        product.addImage("https://example.com/image.jpg");

        // When
        ProductEntity entity = ProductEntityMapper.toEntity(product);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(product.getId().getValue());
        assertThat(entity.getName()).isEqualTo(product.getName());
        assertThat(entity.getDescription()).isEqualTo(product.getDescription());
        assertThat(entity.getPriceAmount()).isEqualByComparingTo(product.getPrice().getAmount());
        assertThat(entity.getPriceCurrency()).isEqualTo(product.getPrice().getCurrency().getCurrencyCode());
        assertThat(entity.getSku()).isEqualTo(product.getSku());
        assertThat(entity.getStockQuantity()).isEqualTo(product.getStockQuantity().getValue());
        assertThat(entity.getStatus()).isEqualTo(product.getStatus().name());
        assertThat(entity.getCategoryIds()).contains(categoryId.getValue());
        assertThat(entity.getImages()).contains("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("Should map entity to product")
    void shouldMapEntityToProduct() {
        // Given
        ProductEntity entity = ProductEntity.builder()
            .id(ID.generate().getValue())
            .name("Test Product")
            .description("Test Description")
            .priceAmount(new BigDecimal("99.99"))
            .priceCurrency("USD")
            .sku("PROD-001")
            .stockQuantity(10)
            .status(ProductStatus.ACTIVE.name())
            .build();

        // When
        Product product = ProductEntityMapper.toDomain(entity);

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getId().getValue()).isEqualTo(entity.getId());
        assertThat(product.getName()).isEqualTo(entity.getName());
        assertThat(product.getDescription()).isEqualTo(entity.getDescription());
        assertThat(product.getPrice().getAmount()).isEqualByComparingTo(entity.getPriceAmount());
        assertThat(product.getPrice().getCurrency().getCurrencyCode()).isEqualTo(entity.getPriceCurrency());
        assertThat(product.getSku()).isEqualTo(entity.getSku());
        assertThat(product.getStockQuantity().getValue()).isEqualTo(entity.getStockQuantity());
        assertThat(product.getStatus().name()).isEqualTo(entity.getStatus());
    }

    @Test
    @DisplayName("Should map product with weight to entity and back")
    void shouldMapProductWithWeightToEntityAndBack() {
        // Given
        Product product = Product.create(
            "Test Product",
            Money.of(new BigDecimal("99.99"), USD),
            "PROD-001"
        );
        Weight weight = Weight.of(new BigDecimal("1.5"), Weight.WeightUnit.KILOGRAM);
        product.setWeight(weight);

        // When
        ProductEntity entity = ProductEntityMapper.toEntity(product);
        Product mappedProduct = ProductEntityMapper.toDomain(entity);

        // Then
        assertThat(mappedProduct.getWeight()).isNotNull();
        assertThat(mappedProduct.getWeight().getValue()).isEqualByComparingTo(weight.getValue());
        assertThat(mappedProduct.getWeight().getUnit()).isEqualTo(weight.getUnit());
    }

    @Test
    @DisplayName("Should map product with dimensions to entity and back")
    void shouldMapProductWithDimensionsToEntityAndBack() {
        // Given
        Product product = Product.create(
            "Test Product",
            Money.of(new BigDecimal("99.99"), USD),
            "PROD-001"
        );
        Dimensions dimensions = Dimensions.of(
            new BigDecimal("10"),
            new BigDecimal("20"),
            new BigDecimal("30"),
            Dimensions.LengthUnit.CENTIMETER
        );
        product.setDimensions(dimensions);

        // When
        ProductEntity entity = ProductEntityMapper.toEntity(product);
        Product mappedProduct = ProductEntityMapper.toDomain(entity);

        // Then
        assertThat(mappedProduct.getDimensions()).isNotNull();
        assertThat(mappedProduct.getDimensions().getLength()).isEqualByComparingTo(dimensions.getLength());
        assertThat(mappedProduct.getDimensions().getWidth()).isEqualByComparingTo(dimensions.getWidth());
        assertThat(mappedProduct.getDimensions().getHeight()).isEqualByComparingTo(dimensions.getHeight());
        assertThat(mappedProduct.getDimensions().getUnit()).isEqualTo(dimensions.getUnit());
    }
}

