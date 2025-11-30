package com.hexagonal.framework.adapter.output.persistence.h2;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.framework.adapter.output.persistence.h2.entity.ProductJpaEntity;
import com.hexagonal.framework.adapter.output.persistence.h2.mapper.ProductJpaMapper;
import com.hexagonal.framework.adapter.output.persistence.h2.repository.ProductJpaRepository;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductH2Adapter Tests")
class ProductH2AdapterTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @InjectMocks
    private ProductH2Adapter adapter;

    private Product product;
    private ProductJpaEntity entity;
    private ID productId;
    private Currency USD;

    @BeforeEach
    void setUp() {
        USD = Currency.getInstance("USD");
        productId = ID.generate();
        product = Product.of(
            productId,
            "Test Product",
            Money.of(new BigDecimal("99.99"), USD),
            "PROD-001",
            ProductStatus.ACTIVE
        );
        entity = ProductJpaMapper.toEntity(product);
    }

    @Test
    @DisplayName("Should save product")
    void shouldSaveProduct() {
        // Given
        when(jpaRepository.save(any(ProductJpaEntity.class))).thenReturn(entity);

        // When
        Product result = adapter.save(product);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        verify(jpaRepository).save(any(ProductJpaEntity.class));
    }

    @Test
    @DisplayName("Should find product by id")
    void shouldFindProductById() {
        // Given
        when(jpaRepository.findById(productId.getValue())).thenReturn(Optional.of(entity));

        // When
        Optional<Product> result = adapter.findById(productId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(productId);
        verify(jpaRepository).findById(productId.getValue());
    }

    @Test
    @DisplayName("Should return empty when product not found by id")
    void shouldReturnEmptyWhenProductNotFoundById() {
        // Given
        when(jpaRepository.findById(productId.getValue())).thenReturn(Optional.empty());

        // When
        Optional<Product> result = adapter.findById(productId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRepository).findById(productId.getValue());
    }

    @Test
    @DisplayName("Should find product by SKU")
    void shouldFindProductBySku() {
        // Given
        String sku = "PROD-001";
        when(jpaRepository.findBySku(sku)).thenReturn(Optional.of(entity));

        // When
        Optional<Product> result = adapter.findBySku(sku);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSku()).isEqualTo(sku);
        verify(jpaRepository).findBySku(sku);
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        // Given
        ProductJpaEntity entity2 = ProductJpaMapper.toEntity(
            Product.create("Product 2", Money.of(new BigDecimal("49.99"), USD), "PROD-002")
        );
        when(jpaRepository.findAll()).thenReturn(List.of(entity, entity2));

        // When
        List<Product> result = adapter.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Should delete product by id")
    void shouldDeleteProductById() {
        // When
        adapter.deleteById(productId);

        // Then
        verify(jpaRepository).deleteById(productId.getValue());
    }

    @Test
    @DisplayName("Should check if product exists by id")
    void shouldCheckIfProductExistsById() {
        // Given
        when(jpaRepository.existsById(productId.getValue())).thenReturn(true);

        // When
        boolean result = adapter.existsById(productId);

        // Then
        assertThat(result).isTrue();
        verify(jpaRepository).existsById(productId.getValue());
    }

    @Test
    @DisplayName("Should check if product exists by SKU")
    void shouldCheckIfProductExistsBySku() {
        // Given
        String sku = "PROD-001";
        when(jpaRepository.existsBySku(sku)).thenReturn(true);

        // When
        boolean result = adapter.existsBySku(sku);

        // Then
        assertThat(result).isTrue();
        verify(jpaRepository).existsBySku(sku);
    }
}

