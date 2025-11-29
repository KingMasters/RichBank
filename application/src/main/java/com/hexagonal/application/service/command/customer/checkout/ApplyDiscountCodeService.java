package com.hexagonal.application.service.command.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ApplyDiscountCodeCommand;
import com.hexagonal.application.port.in.customer.checkout.ApplyDiscountCodeUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Apply Discount Code Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den sepeti alır
 * - İndirim kodunu doğrular
 * - Sepete indirimi uygular
 */
@UseCase
public class ApplyDiscountCodeService implements ApplyDiscountCodeUseCase {
    private final CartRepositoryPort cartRepository;

    public ApplyDiscountCodeService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * İndirim kodu uygulama use case'i
     * 1. Sepeti repository'den al
     * 2. İndirim kodunu doğrula
     * 3. Sepete indirimi uygula
     *
     * NOT: Gerçek uygulamada indirim yönetimi discount service tarafından yapılmalıdır
     */
    @Override
    public Cart execute(ApplyDiscountCodeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("ApplyDiscountCodeCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Sepeti repository'den al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // İndirim kodunu doğrula
        if (command.getDiscountCode() == null || command.getDiscountCode().isBlank()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }

        // NOT: Gerçek uygulamada indirim kodu doğrulaması ve uygulanması
        // bir discount service tarafından handle edilir

        return cart;
    }
}

