package com.hexagonal.application.service.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.application.port.in.customer.cart.ViewCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ViewCartService implements ViewCartUseCase {
    private final CartRepositoryPort cartRepository;

    public ViewCartService(CartRepositoryPort cartRepository) {
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

