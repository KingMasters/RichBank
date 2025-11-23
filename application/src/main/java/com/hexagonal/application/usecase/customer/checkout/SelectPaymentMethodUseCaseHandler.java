package com.hexagonal.application.usecase.customer.checkout;

import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.application.port.in.customer.checkout.SelectPaymentMethodUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class SelectPaymentMethodUseCaseHandler implements SelectPaymentMethodUseCase {
    private final CartRepositoryPort cartRepository;

    public SelectPaymentMethodUseCaseHandler(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart execute(SelectPaymentMethodCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SelectPaymentMethodCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Validate payment method
        if (command.getPaymentMethod() == null || command.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        // Note: In a real implementation, payment method would be stored
        // in a checkout context or session, not directly in the cart
        // For this hexagon structure, we're just validating the cart exists

        return cart;
    }
}

