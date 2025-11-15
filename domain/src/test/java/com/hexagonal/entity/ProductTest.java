package com.hexagonal.entity;

import com.hexagonal.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Product Entity Tests")
class ProductTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Money VALID_PRICE = Money.of(new BigDecimal("99.99"), USD);
    private static final String VALID_SKU = "PROD-001";
    private static final String VALID_NAME = "Test Product";

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProductWithValidData() {
        // When
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo(VALID_NAME);
        assertThat(product.getPrice()).isEqualTo(VALID_PRICE);
        assertThat(product.getSku()).isEqualTo(VALID_SKU);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.getStockQuantity()).isEqualTo(Quantity.zero());
        assertThat(product.getCategoryIds()).isEmpty();
        assertThat(product.getImages()).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        // Then
        assertThatThrownBy(() -> Product.create(null, VALID_PRICE, VALID_SKU))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        // Then
        assertThatThrownBy(() -> Product.create(VALID_NAME, null, VALID_SKU))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product price cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when price is zero")
    void shouldThrowExceptionWhenPriceIsZero() {
        // Given
        Money zeroPrice = Money.zero(USD);

        // Then
        assertThatThrownBy(() -> Product.create(VALID_NAME, zeroPrice, VALID_SKU))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product price must be positive");
    }

    @Test
    @DisplayName("Should throw exception when SKU is null")
    void shouldThrowExceptionWhenSkuIsNull() {
        // Then
        assertThatThrownBy(() -> Product.create(VALID_NAME, VALID_PRICE, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product SKU cannot be null or empty");
    }

    @Test
    @DisplayName("Should update product name")
    void shouldUpdateProductName() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        String newName = "Updated Product Name";

        // When
        product.updateName(newName);

        // Then
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getUpdatedAt()).isAfter(product.getCreatedAt());
    }

    @Test
    @DisplayName("Should update product description")
    void shouldUpdateProductDescription() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        String description = "Test description";

        // When
        product.updateDescription(description);

        // Then
        assertThat(product.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should update product price")
    void shouldUpdateProductPrice() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Money newPrice = Money.of(new BigDecimal("149.99"), USD);

        // When
        product.updatePrice(newPrice);

        // Then
        assertThat(product.getPrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Should add category to product")
    void shouldAddCategoryToProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        ID categoryId = ID.generate();

        // When
        product.addCategory(categoryId);

        // Then
        assertThat(product.getCategoryIds()).contains(categoryId);
        assertThat(product.hasCategory(categoryId)).isTrue();
    }

    @Test
    @DisplayName("Should add multiple categories to product")
    void shouldAddMultipleCategoriesToProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Set<ID> categoryIds = Set.of(ID.generate(), ID.generate(), ID.generate());

        // When
        product.addCategories(categoryIds);

        // Then
        assertThat(product.getCategoryIds()).containsAll(categoryIds);
    }

    @Test
    @DisplayName("Should add stock to product")
    void shouldAddStockToProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Quantity quantity = Quantity.of(10);

        // When
        product.addStock(quantity);

        // Then
        assertThat(product.getStockQuantity()).isEqualTo(quantity);
        assertThat(product.isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should remove stock from product")
    void shouldRemoveStockFromProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        product.addStock(Quantity.of(10));

        // When
        product.removeStock(Quantity.of(5));

        // Then
        assertThat(product.getStockQuantity()).isEqualTo(Quantity.of(5));
    }

    @Test
    @DisplayName("Should throw exception when removing more stock than available")
    void shouldThrowExceptionWhenRemovingMoreStockThanAvailable() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        product.addStock(Quantity.of(10));

        // Then
        assertThatThrownBy(() -> product.removeStock(Quantity.of(15)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should mark product as out of stock when stock becomes zero")
    void shouldMarkProductAsOutOfStockWhenStockBecomesZero() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        product.addStock(Quantity.of(10));

        // When
        product.removeStock(Quantity.of(10));

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
        assertThat(product.isOutOfStock()).isTrue();
    }

    @Test
    @DisplayName("Should mark product as active when stock is added to out of stock product")
    void shouldMarkProductAsActiveWhenStockIsAddedToOutOfStockProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        product.setStock(Quantity.zero());

        // When
        product.addStock(Quantity.of(5));

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should add image to product")
    void shouldAddImageToProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        String imageUrl = "https://example.com/image.jpg";

        // When
        product.addImage(imageUrl);

        // Then
        assertThat(product.getImages()).contains(imageUrl);
    }

    @Test
    @DisplayName("Should calculate total price for quantity")
    void shouldCalculateTotalPriceForQuantity() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Quantity quantity = Quantity.of(3);

        // When
        Money totalPrice = product.calculateTotalPrice(quantity);

        // Then
        assertThat(totalPrice.getAmount()).isEqualByComparingTo(new BigDecimal("299.97"));
        assertThat(totalPrice.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should set weight on product")
    void shouldSetWeightOnProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Weight weight = Weight.of(new BigDecimal("1.5"), Weight.WeightUnit.KILOGRAM);

        // When
        product.setWeight(weight);

        // Then
        assertThat(product.getWeight()).isEqualTo(weight);
    }

    @Test
    @DisplayName("Should set dimensions on product")
    void shouldSetDimensionsOnProduct() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);
        Dimensions dimensions = Dimensions.of(
            new BigDecimal("10"),
            new BigDecimal("20"),
            new BigDecimal("30"),
            Dimensions.LengthUnit.CENTIMETER
        );

        // When
        product.setDimensions(dimensions);

        // Then
        assertThat(product.getDimensions()).isEqualTo(dimensions);
    }

    @Test
    @DisplayName("Should mark product as discontinued")
    void shouldMarkProductAsDiscontinued() {
        // Given
        Product product = Product.create(VALID_NAME, VALID_PRICE, VALID_SKU);

        // When
        product.markAsDiscontinued();

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
        assertThat(product.isDiscontinued()).isTrue();
    }
}

