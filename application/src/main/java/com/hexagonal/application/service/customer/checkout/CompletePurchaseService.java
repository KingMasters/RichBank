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
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.ID;

import java.util.ArrayList;
import java.util.List;

@UseCase
public class CompletePurchaseService implements CompletePurchaseUseCase {
    private final CartRepositoryPort cartRepository;
    private final CustomerRepositoryPort customerRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;

    public CompletePurchaseService(
            CartRepositoryPort cartRepository,
            CustomerRepositoryPort customerRepository,
            OrderRepositoryPort orderRepository,
            ProductRepositoryPort productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order execute(CompletePurchaseCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("CompletePurchaseCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());

        // Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot complete purchase with empty cart");
        }

        // Convert cart items to order items and validate stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (var cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product", cartItem.getProductId()));

            // Verify stock availability
            if (!product.hasStock(cartItem.getQuantity())) {
                throw new InsufficientStockException(
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        product.getStockQuantity()
                );
            }

            // Create order item
            OrderItem orderItem = OrderItem.create(
                    cartItem.getProductId(),
                    product.getName(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
            );
            orderItems.add(orderItem);

            // Reserve stock (remove from inventory)
            product.removeStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        // Get shipping address (use customer's address or from command)
        Address shippingAddress = customer.getAddress();
        if (shippingAddress == null) {
            throw new IllegalStateException("Shipping address is required to complete purchase");
        }

        // Get billing address (use shipping address if not provided separately)
        Address billingAddress = shippingAddress;

        // Create order
        Order order = Order.create(customerId, orderItems, shippingAddress, billingAddress);

        // Apply discount if provided (this would typically be handled by a discount service)
        // For now, we'll just create the order with the cart total

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Convert cart to order (mark cart as converted)
        cart.convertToOrder();
        cartRepository.save(cart);

        return savedOrder;
    }
}

