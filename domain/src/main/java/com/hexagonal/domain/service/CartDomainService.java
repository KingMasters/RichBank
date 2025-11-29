package com.hexagonal.domain.service;

import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.InsufficientStockException;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

/**
 * Domain Service for Cart Entity
 * Sadece domain business logic'ini handle eder, persistence ile ilgilenmez
 * Port'a bağımlı değildir
 */
public class CartDomainService {

    /**
     * Domain logic: Sepete ürün ekle
     * Stock kontrolü yapılarak ürün eklenir
     */
    public void addProductToCart(Cart cart, Product product, Quantity quantity) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null || quantity.getValue() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (!product.isActive()) {
            throw new IllegalStateException("Product is not active");
        }
        if (product.getStockQuantity().getValue() < quantity.getValue()) {
            throw new InsufficientStockException(product.getId(), quantity, product.getStockQuantity());
        }

        cart.addItem(product.getId(), quantity, product.getPrice());
    }

    /**
     * Domain logic: Sepetten ürün çıkar
     */
    public void removeProductFromCart(Cart cart, ID productId) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        cart.removeItem(productId);
    }

    /**
     * Domain logic: Sepetteki ürün miktarını güncelle
     */
    public void updateProductQuantityInCart(Cart cart, Product product, Quantity newQuantity) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (newQuantity == null || newQuantity.getValue() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (product.getStockQuantity().getValue() < newQuantity.getValue()) {
            throw new InsufficientStockException(product.getId(), newQuantity, product.getStockQuantity());
        }

        cart.updateItemQuantity(product.getId(), newQuantity);
    }

    /**
     * Domain logic: Sepeti temizle
     */
    public void clearCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        cart.clear();
    }
}

