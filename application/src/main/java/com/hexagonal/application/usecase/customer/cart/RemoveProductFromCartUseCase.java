package com.hexagonal.application.usecase.customer.cart;

import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.application.port.out.CartRepositoryOutputPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class RemoveProductFromCartUseCase implements com.hexagonal.application.port.in.customer.cart.RemoveProductFromCartInputPort {
    private final CartRepositoryOutputPort cartRepository;

    public RemoveProductFromCartUseCase(CartRepositoryOutputPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart execute(RemoveProductFromCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("RemoveProductFromCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Remove item from cart
        cart.removeItem(productId);

        return cartRepository.save(cart);
    }
}

