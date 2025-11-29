package com.hexagonal.domain.entity;

import com.hexagonal.domain.vo.Dimensions;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.ProductStatus;
import com.hexagonal.domain.vo.Quantity;
import com.hexagonal.domain.vo.Weight;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Product {
    private final ID id;
    private String name;
    private String description;
    private Money price;
    private Quantity stockQuantity;
    private Set<ID> categoryIds;
    private List<String> images;
    /// sku: stock keeping unit : bir ürünü dahili olarak tanımlamak ve takip etmek için kullandığı, genellikle harf ve rakamlardan oluşan benzersiz bir koddur
    private String sku;
    private ProductStatus status;
    private Weight weight;
    private Dimensions dimensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product(ID id, String name, Money price, String sku) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }
        if (price.isNegative() || price.isZero()) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.sku = sku.trim().toUpperCase();
        this.stockQuantity = Quantity.zero();
        this.categoryIds = new HashSet<>();
        this.images = new ArrayList<>();
        this.status = ProductStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Product create(String name, Money price, String sku) {
        return new Product(ID.generate(), name, price, sku);
    }

    public static Product of(ID id, String name, Money price, String sku) {
        return new Product(id, name, price, sku);
    }

    public static Product of(ID id, String name, Money price, String sku, ProductStatus status) {
        Product product = new Product(id, name, price, sku);
        product.status = status;
        return product;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = name.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (newPrice.isNegative() || newPrice.isZero()) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void addCategory(ID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        this.categoryIds.add(categoryId);
        this.updatedAt = LocalDateTime.now();
    }

    public void addCategories(Set<ID> categoryIds) {
        if (categoryIds == null) {
            throw new IllegalArgumentException("Category IDs cannot be null");
        }
        categoryIds.forEach(categoryId -> {
            if (categoryId == null) {
                throw new IllegalArgumentException("Category ID cannot be null");
            }
        });
        this.categoryIds.addAll(categoryIds);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeCategory(ID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        this.categoryIds.remove(categoryId);
        this.updatedAt = LocalDateTime.now();
    }

    public void clearCategories() {
        this.categoryIds.clear();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasCategory(ID categoryId) {
        return categoryIds.contains(categoryId);
    }

    public Set<ID> getCategoryIds() {
        return Collections.unmodifiableSet(categoryIds);
    }

    public boolean hasAnyCategory() {
        return !categoryIds.isEmpty();
    }

    public void addStock(Quantity quantity) {
        if (quantity == null || quantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity = this.stockQuantity.add(quantity);
        // If stock was zero and now has stock, and status is OUT_OF_STOCK, change to ACTIVE
        if (status == ProductStatus.OUT_OF_STOCK && stockQuantity.isPositive()) {
            this.status = ProductStatus.ACTIVE;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void removeStock(Quantity quantity) {
        if (quantity == null || quantity.isZero()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockQuantity.isLessThan(quantity)) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockQuantity = this.stockQuantity.subtract(quantity);
        // If stock becomes zero and status is ACTIVE, change to OUT_OF_STOCK
        if (status == ProductStatus.ACTIVE && stockQuantity.isZero()) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void setStock(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        this.stockQuantity = quantity;
        // Update status based on stock
        if (quantity.isZero() && status == ProductStatus.ACTIVE) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (quantity.isPositive() && status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ACTIVE;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank() && !images.contains(imageUrl)) {
            this.images.add(imageUrl.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeImage(String imageUrl) {
        this.images.remove(imageUrl);
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void markAsActive() {
        if (!status.canTransitionTo(ProductStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot mark product as active with status: " + status);
        }
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDiscontinued() {
        if (!status.canTransitionTo(ProductStatus.DISCONTINUED)) {
            throw new IllegalStateException("Cannot mark product as discontinued with status: " + status);
        }
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsOutOfStock() {
        if (!status.canTransitionTo(ProductStatus.OUT_OF_STOCK)) {
            throw new IllegalStateException("Cannot mark product as out of stock with status: " + status);
        }
        this.status = ProductStatus.OUT_OF_STOCK;
        this.updatedAt = LocalDateTime.now();
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeWeight() {
        this.weight = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeDimensions() {
        this.dimensions = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isInStock() {
        return stockQuantity.isPositive();
    }

    public boolean hasStock(Quantity quantity) {
        return stockQuantity.isGreaterThan(quantity) || stockQuantity.equals(quantity);
    }

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

    public boolean isDiscontinued() {
        return status == ProductStatus.DISCONTINUED;
    }

    public boolean isOutOfStock() {
        return status == ProductStatus.OUT_OF_STOCK;
    }

    public Money calculateTotalPrice(Quantity quantity) {
        return price.multiply(java.math.BigDecimal.valueOf(quantity.getValue()));
    }
}
