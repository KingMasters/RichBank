package com.hexagonal.application.service.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ApplyDiscountCodeCommand;
import com.hexagonal.application.port.in.customer.checkout.ApplyDiscountCodeUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ApplyDiscountCodeService implements ApplyDiscountCodeUseCase {
    private final CartRepositoryPort cartRepository;

    public ApplyDiscountCodeService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart execute(ApplyDiscountCodeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("ApplyDiscountCodeCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Validate discount code
        if (command.getDiscountCode() == null || command.getDiscountCode().isBlank()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }

        // Note: In a real implementation, discount code validation and application
        // would be handled by a discount service. For this hexagon structure,
        // we're just validating the cart exists and the code is provided

        return cart;
    }
}

