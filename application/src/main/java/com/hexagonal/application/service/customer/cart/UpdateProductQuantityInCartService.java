package com.hexagonal.application.service.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.port.in.customer.cart.UpdateProductQuantityInCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.exception.InsufficientStockException;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

@UseCase
public class UpdateProductQuantityInCartService implements UpdateProductQuantityInCartUseCase {
    private final CartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;

    public UpdateProductQuantityInCartService(CartRepositoryPort cartRepository, ProductRepositoryPort productRepository) {
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

