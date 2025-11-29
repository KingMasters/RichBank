package com.hexagonal.domain.specification;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.ProductStatus;

import java.util.Set;

public class ProductSpecifications {
    
    public static Specification<Product> isActive() {
        return Product::isActive;
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return product -> product.getStatus().equals(status);
    }

    public static Specification<Product> isDiscontinued() {
        return Product::isDiscontinued;
    }

    public static Specification<Product> isOutOfStock() {
        return Product::isOutOfStock;
    }

    public static Specification<Product> isInStock() {
        return Product::isInStock;
    }

    public static Specification<Product> hasCategory(ID categoryId) {
        return product -> product.hasCategory(categoryId);
    }

    public static Specification<Product> hasAnyCategory(Set<ID> categoryIds) {
        return product -> categoryIds.stream().anyMatch(product::hasCategory);
    }

    public static Specification<Product> hasAllCategories(Set<ID> categoryIds) {
        return product -> product.getCategoryIds().containsAll(categoryIds);
    }

    public static Specification<Product> priceLessThan(Money maxPrice) {
        return product -> product.getPrice().isLessThan(maxPrice) || product.getPrice().equals(maxPrice);
    }

    public static Specification<Product> priceGreaterThan(Money minPrice) {
        return product -> product.getPrice().isGreaterThan(minPrice) || product.getPrice().equals(minPrice);
    }

    public static Specification<Product> priceBetween(Money minPrice, Money maxPrice) {
        return priceGreaterThan(minPrice).and(priceLessThan(maxPrice));
    }

    public static Specification<Product> hasSku(String sku) {
        return product -> product.getSku().equalsIgnoreCase(sku);
    }

    public static Specification<Product> nameContains(String searchTerm) {
        return product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase());
    }

    public static Specification<Product> isActiveAndInStock() {
        return isActive().and(isInStock());
    }
}

