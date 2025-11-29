package com.hexagonal.application.service.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.application.port.out.OrderRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.entity.OrderItem;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.exception.InsufficientStockException;
import com.hexagonal.domain.service.ProductDomainService;
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.ID;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Service - Complete Purchase Use Case Implementation
 *
 * Karmaşık Orkestrasyon Servisi:
 * - Müşteri ve sepet bilgisini alır
 * - Stok kontrolü yapar
 * - ProductDomainService'i çağırarak stok azaltır
 * - Order entity'sini oluşturur
 * - Sepeti sipariş haline dönüştürür
 * - Tüm değişiklikleri kaydeder
 */
@UseCase
public class CompletePurchaseService implements CompletePurchaseUseCase {
    private final CartRepositoryPort cartRepository;
    private final CustomerRepositoryPort customerRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final ProductDomainService productDomainService;

    public CompletePurchaseService(
            CartRepositoryPort cartRepository,
            CustomerRepositoryPort customerRepository,
            OrderRepositoryPort orderRepository,
            ProductRepositoryPort productRepository,
            ProductDomainService productDomainService) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    /**
     * Satın alım tamamlama use case'i
     * 1. Müşteri ve sepeti al
     * 2. Sepet boş olmadığını kontrol et
     * 3. Sepet ürünlerini sipariş ürünlerine dönüştür ve stok kontrol et
     * 4. ProductDomainService kullanarak stok azalt
     * 5. Order entity'sini oluştur
     * 6. Sepeti sipariş haline dönüştür
     * 7. Tüm değişiklikleri kaydet
     */
    @Override
    public Order execute(CompletePurchaseCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("CompletePurchaseCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Müşteri bilgisini al
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Sepeti al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot complete purchase with empty cart");
        }

        // Sepet ürünlerini sipariş ürünlerine dönüştür ve stok kontrol et
        List<OrderItem> orderItems = new ArrayList<>();
        for (var cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product", cartItem.getProductId()));

            // Stok doğrulaması
            if (!product.hasStock(cartItem.getQuantity())) {
                throw new InsufficientStockException(
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        product.getStockQuantity()
                );
            }

            // Sipariş ürünü oluştur
            OrderItem orderItem = OrderItem.create(
                    cartItem.getProductId(),
                    product.getName(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
            );
            orderItems.add(orderItem);

            // Domain service kullanarak stok azalt
            productDomainService.removeStock(product, cartItem.getQuantity());
            productRepository.save(product);
        }

        // Gönderim adresini al
        Address shippingAddress = customer.getAddress();
        if (shippingAddress == null) {
            throw new IllegalStateException("Shipping address is required to complete purchase");
        }

        // Order entity'sini oluştur (gönderim adresi fatura adresi olarak da kullanılır)
        Order order = Order.create(customerId, orderItems, shippingAddress, shippingAddress);

        // Order'ı kaydet
        Order savedOrder = orderRepository.save(order);

        // Sepeti sipariş haline dönüştür
        cart.convertToOrder();
        cartRepository.save(cart);

        return savedOrder;
    }
}

