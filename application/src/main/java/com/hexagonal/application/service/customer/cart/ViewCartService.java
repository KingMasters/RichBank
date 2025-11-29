package com.hexagonal.application.service.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.customer.cart.ViewCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - View Cart Use Case Implementation
 *
 * Query Service:
 * - Repository'den sepeti alır
 * - Müşterinin sepetini döndürür
 */
@UseCase
public class ViewCartService implements ViewCartUseCase {
    private final CartRepositoryPort cartRepository;

    public ViewCartService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Sepeti görüntüleme use case'i
     * - Müşteri kimliğine göre sepeti al
     */
    @Override
    public Cart execute(ViewCartQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("ViewCartCommand cannot be null");
        }

        ID customerId = ID.of(query.customerId());
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + query.customerId() + " not found"));
    }
}

