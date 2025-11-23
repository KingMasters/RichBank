package com.hexagonal.application.usecase.customer.checkout;

import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.application.port.in.customer.checkout.EnterShippingInformationUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Customer;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Address;
import com.hexagonal.vo.ID;

public class EnterShippingInformationUseCaseHandler implements EnterShippingInformationUseCase {
    private final CartRepositoryPort cartRepository;
    private final CustomerRepositoryPort customerRepository;

    public EnterShippingInformationUseCaseHandler(CartRepositoryPort cartRepository, CustomerRepositoryPort customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Cart execute(EnterShippingInformationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("EnterShippingInformationCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        
        // Verify customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Find cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Create shipping address
        Address shippingAddress = Address.of(
                command.getStreet(),
                command.getCity(),
                command.getState(),
                command.getZipCode(),
                command.getCountry()
        );

        // Update customer address (this could be stored separately for shipping)
        customer.updateAddress(shippingAddress);
        customerRepository.save(customer);

        // Note: In a real implementation, shipping information might be stored
        // separately or in a session/checkout context rather than directly in the cart
        // For this hexagon structure, we're updating the customer's address

        return cart;
    }
}

