package com.hexagonal.application.ports.input.customer.cart;

import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.application.ports.output.CartRepositoryOutputPort;
import com.hexagonal.application.usecases.customer.cart.ViewCartUseCase;
import com.hexagonal.entity.Cart;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ViewCartInputPort implements ViewCartUseCase {
    private final CartRepositoryOutputPort cartRepository;

    public ViewCartInputPort(CartRepositoryOutputPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart execute(ViewCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("ViewCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));
    }
}

