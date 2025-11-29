package com.hexagonal.application.service.command.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.application.port.in.customer.cart.RemoveProductFromCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.CartDomainService;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Remove Product From Cart Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den sepeti alır
 * - CartDomainService'i çağırarak domain logic'i uygular
 * - Güncellenmiş sepeti repository'ye kaydeder
 */
@UseCase
public class RemoveProductFromCartService implements RemoveProductFromCartUseCase {
    private final CartRepositoryPort cartRepository;
    private final CartDomainService cartDomainService;

    public RemoveProductFromCartService(CartRepositoryPort cartRepository,
                                        CartDomainService cartDomainService) {
        this.cartRepository = cartRepository;
        this.cartDomainService = cartDomainService;
    }

    /**
     * Sepetten ürün çıkarma use case'i
     * 1. Sepeti repository'den al
     * 2. Domain service'i çağırarak ürünü çıkar
     * 3. Güncellenmiş sepeti kaydet
     */
    @Override
    public Cart execute(RemoveProductFromCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("RemoveProductFromCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Sepeti repository'den al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Domain service'i çağırarak ürünü çıkar
        cartDomainService.removeProductFromCart(cart, productId);

        return cartRepository.save(cart);
    }
}

