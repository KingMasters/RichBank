package com.hexagonal.application.service.customer.cart;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.CartDomainService;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

/**
 * Application Service - Add Product To Cart Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den ürün ve sepeti alır
 * - CartDomainService'i çağırarak domain logic'i uygular
 * - Güncellenmiş sepeti repository'ye kaydeder
 */
@UseCase
public class AddProductToCartService implements AddProductToCartUseCase {
    private final CartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;
    private final CartDomainService cartDomainService;

    public AddProductToCartService(CartRepositoryPort cartRepository,
                                   ProductRepositoryPort productRepository,
                                   CartDomainService cartDomainService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartDomainService = cartDomainService;
    }

    /**
     * Sepete ürün ekleme use case'i
     * 1. Sepeti al veya oluştur
     * 2. Ürünü repository'den al
     * 3. Domain service'i çağırarak sepete ürün ekle
     * 4. Güncellenmiş sepeti kaydet
     */
    @Override
    public Cart execute(AddProductToCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("AddProductToCartCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        ID productId = ID.of(command.getProductId());

        // Sepeti al veya oluştur
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(customerId);
                    return cartRepository.save(newCart);
                });

        // Ürünü repository'den al
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        // Domain service'i çağırarak sepete ürün ekle
        Quantity requestedQuantity = Quantity.of(command.getQuantity());
        cartDomainService.addProductToCart(cart, product, requestedQuantity);

        return cartRepository.save(cart);
    }
}

