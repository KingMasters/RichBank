package com.hexagonal.application.service.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.port.in.customer.cart.UpdateProductQuantityInCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.CartDomainService;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

/**
 * Application Service - Update Product Quantity In Cart Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den sepeti ve ürünü alır
 * - CartDomainService'i çağırarak domain logic'i uygular
 * - Güncellenmiş sepeti repository'ye kaydeder
 */
@UseCase
public class UpdateProductQuantityInCartService implements UpdateProductQuantityInCartUseCase {
    private final CartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;
    private final CartDomainService cartDomainService;

    public UpdateProductQuantityInCartService(CartRepositoryPort cartRepository,
                                               ProductRepositoryPort productRepository,
                                               CartDomainService cartDomainService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartDomainService = cartDomainService;
    }

    /**
     * Sepetteki ürün miktarını güncelleme use case'i
     * 1. Sepeti repository'den al
     * 2. Ürünü repository'den al
     * 3. Domain service'i çağırarak miktarı güncelle
     * 4. Güncellenmiş sepeti kaydet
     */
    @Override
    public Cart execute(UpdateProductQuantityInCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("UpdateProductQuantityInCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Sepeti repository'den al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Ürünü repository'den al
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        // Domain service'i çağırarak miktarı güncelle
        Quantity newQuantity = Quantity.of(command.getQuantity());
        cartDomainService.updateProductQuantityInCart(cart, product, newQuantity);

        return cartRepository.save(cart);
    }
}

