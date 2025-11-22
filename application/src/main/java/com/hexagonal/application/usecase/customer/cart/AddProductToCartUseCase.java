package com.hexagonal.application.usecase.customer.cart;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.port.out.CartRepositoryOutputPort;
import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.exception.InsufficientStockException;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.Quantity;

public class AddProductToCartUseCase implements com.hexagonal.application.port.in.customer.cart.AddProductToCartInputPort {
    private final CartRepositoryOutputPort cartRepository;
    private final ProductRepositoryOutputPort productRepository;

    public AddProductToCartUseCase(CartRepositoryOutputPort cartRepository, ProductRepositoryOutputPort productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart execute(AddProductToCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("AddProductToCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Find or create cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(customerId);
                    return cartRepository.save(newCart);
                });

        // Verify product exists and is active
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        if (!product.isActive()) {
            throw new IllegalStateException("Product is not active");
        }

        // Check stock availability
        Quantity requestedQuantity = Quantity.of(command.getQuantity());
        if (!product.hasStock(requestedQuantity)) {
            throw new InsufficientStockException(productId, requestedQuantity, product.getStockQuantity());
        }

        // Get product price
        Money unitPrice = product.getPrice();
        
        // Add item to cart
        cart.addItem(productId, requestedQuantity, unitPrice);

        return cartRepository.save(cart);
    }
}

