package com.hexagonal.application.usecase.customer.cart;

import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.application.port.in.customer.cart.ViewCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

public class ViewCartUseCaseHandler implements ViewCartUseCase {
    private final CartRepositoryPort cartRepository;

    public ViewCartUseCaseHandler(CartRepositoryPort cartRepository) {
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

