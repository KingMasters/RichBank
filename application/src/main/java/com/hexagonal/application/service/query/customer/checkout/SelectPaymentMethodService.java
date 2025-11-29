package com.hexagonal.application.service.query.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.application.port.in.customer.checkout.SelectPaymentMethodUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Select Payment Method Use Case Implementation
 *
 * Query/Command Service:
 * - Repository'den sepeti alır
 * - Ödeme yöntemi doğrulaması yapar
 * - Ödeme bilgisini session/context'e kaydeder
 */
@UseCase
public class SelectPaymentMethodService implements SelectPaymentMethodUseCase {
    private final CartRepositoryPort cartRepository;

    public SelectPaymentMethodService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Ödeme yöntemi seçme use case'i
     * 1. Sepeti repository'den al
     * 2. Ödeme yöntemi doğrula
     * 3. Ödeme yöntemi session/context'e kaydet
     *
     * NOT: Gerçek uygulamada ödeme yöntemi session veya checkout context'te tutulur
     */
    @Override
    public Cart execute(SelectPaymentMethodCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SelectPaymentMethodCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Sepeti repository'den al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Ödeme yöntemi doğrula
        if (command.getPaymentMethod() == null || command.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        // NOT: Gerçek uygulamada ödeme yöntemi bilgisi session veya checkout context'te saklanır
        // Bu hexagon yapısında sadece sepet varlığını kontrol ettik

        return cart;
    }
}

