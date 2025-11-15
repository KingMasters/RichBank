package com.hexagonal.application.ports.input.admin.product;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.DuplicateEntityException;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductInputPort Tests")
class CreateProductInputPortTest {

    @Mock
    private ProductRepositoryOutputPort productRepository;

    @InjectMocks
    private CreateProductInputPort createProductInputPort;

    private CreateProductCommand command;
    private Currency USD;

    @BeforeEach
    void setUp() {
        USD = Currency.getInstance("USD");
        command = new CreateProductCommand(
            "Test Product",
            "Test Description",
            new BigDecimal("99.99"),
            USD,
            "PROD-001",
            null,  // categoryIds
            null,  // images
            null,  // weight
            null,  // weightUnit
            null,  // length
            null,  // width
            null,  // height
            null   // dimensionUnit
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        Product savedProduct = Product.create(
            command.getName(),
            Money.of(command.getPrice(), command.getCurrency()),
            command.getSku()
        );
        when(productRepository.existsBySku(command.getSku())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = createProductInputPort.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(command.getName());
        assertThat(result.getSku()).isEqualTo(command.getSku());
        verify(productRepository).existsBySku(command.getSku());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when SKU already exists")
    void shouldThrowExceptionWhenSkuAlreadyExists() {
        // Given
        when(productRepository.existsBySku(command.getSku())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> createProductInputPort.execute(command))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("Product")
            .hasMessageContaining("SKU")
            .hasMessageContaining(command.getSku());

        verify(productRepository).existsBySku(command.getSku());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with description")
    void shouldCreateProductWithDescription() {
        // Given
        Product savedProduct = Product.create(
            command.getName(),
            Money.of(command.getPrice(), command.getCurrency()),
            command.getSku()
        );
        savedProduct.updateDescription(command.getDescription());
        
        when(productRepository.existsBySku(command.getSku())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = createProductInputPort.execute(command);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with categories")
    void shouldCreateProductWithCategories() {
        // Given
        ID categoryId1 = ID.generate();
        ID categoryId2 = ID.generate();
        Set<String> categoryIds = Set.of(categoryId1.getValue().toString(), categoryId2.getValue().toString());
        CreateProductCommand commandWithCategories = new CreateProductCommand(
            command.getName(),
            command.getDescription(),
            command.getPrice(),
            command.getCurrency(),
            command.getSku(),
            categoryIds,  // categoryIds
            null,  // images
            null,  // weight
            null,  // weightUnit
            null,  // length
            null,  // width
            null,  // height
            null   // dimensionUnit
        );
        
        Product savedProduct = Product.create(
            commandWithCategories.getName(),
            Money.of(commandWithCategories.getPrice(), commandWithCategories.getCurrency()),
            commandWithCategories.getSku()
        );
        
        when(productRepository.existsBySku(commandWithCategories.getSku())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = createProductInputPort.execute(commandWithCategories);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }
}

