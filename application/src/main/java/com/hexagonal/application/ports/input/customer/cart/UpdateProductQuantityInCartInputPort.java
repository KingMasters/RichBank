package com.hexagonal.application.ports.input.customer.cart;

import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.ports.output.CartRepositoryOutputPort;
import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.cart.UpdateProductQuantityInCartUseCase;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.exception.InsufficientStockException;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Quantity;

public class UpdateProductQuantityInCartInputPort implements UpdateProductQuantityInCartUseCase {
    private final CartRepositoryOutputPort cartRepository;
    private final ProductRepositoryOutputPort productRepository;

    public UpdateProductQuantityInCartInputPort(CartRepositoryOutputPort cartRepository, ProductRepositoryOutputPort productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart execute(UpdateProductQuantityInCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("UpdateProductQuantityInCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

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

        // Update item quantity in cart
        cart.updateItemQuantity(productId, requestedQuantity);

        return cartRepository.save(cart);
    }
}

