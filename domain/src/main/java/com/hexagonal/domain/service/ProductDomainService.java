package com.hexagonal.domain.service;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;
import java.util.Set;

/**
 * Domain Service for Product Entity
 * Sadece domain business logic'ini handle eder, persistence ile ilgilenmez
 * Port'a bağımlı değildir
 */
public class ProductDomainService {

    /**
     * Domain logic: Kategorileri ürüne ekle
     */
    public void assignCategoriesToProduct(Product product, Set<ID> categoryIds) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty");
        }
        product.addCategories(categoryIds);
    }

    /**
     * Domain logic: Stok yönetimi
     */
    public void addStock(Product product, Quantity quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        product.addStock(quantity);
    }

    /**
     * Domain logic: Stok çıkar
     */
    public void removeStock(Product product, Quantity quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        product.removeStock(quantity);
    }

    /**
     * Domain logic: Stok ayarla
     */
    public void setStock(Product product, Quantity quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        product.setStock(quantity);
    }

    /**
     * Domain logic: Ürün açıklaması güncelle
     */
    public void updateDescription(Product product, String description) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        product.updateDescription(description);
    }
}

