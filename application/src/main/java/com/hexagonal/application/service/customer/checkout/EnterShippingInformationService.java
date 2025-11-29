package com.hexagonal.application.service.customer.checkout;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.application.port.in.customer.checkout.EnterShippingInformationUseCase;
import com.hexagonal.application.port.out.CartRepositoryPort;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.CustomerDomainService;
import com.hexagonal.domain.vo.Address;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Enter Shipping Information Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Müşteri ve sepet bilgisini alır
 * - CustomerDomainService kullanarak adresi günceller
 * - Müşteri bilgisini kaydeder
 */
@UseCase
public class EnterShippingInformationService implements EnterShippingInformationUseCase {
    private final CartRepositoryPort cartRepository;
    private final CustomerRepositoryPort customerRepository;
    private final CustomerDomainService customerDomainService;

    public EnterShippingInformationService(CartRepositoryPort cartRepository,
                                           CustomerRepositoryPort customerRepository,
                                           CustomerDomainService customerDomainService) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.customerDomainService = customerDomainService;
    }

    /**
     * Gönderim bilgisi girme use case'i
     * 1. Müşteri mevcudiyetini kontrol et
     * 2. Sepeti al
     * 3. Gönderim adresi oluştur
     * 4. Domain service kullanarak adres güncelle
     * 5. Müşteri bilgisini kaydet
     */
    @Override
    public Cart execute(EnterShippingInformationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("EnterShippingInformationCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        
        // Müşteri mevcudiyetini kontrol et
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Sepeti al
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart for customer " + command.getCustomerId() + " not found"));

        // Gönderim adresi oluştur
        Address shippingAddress = Address.of(
                command.getStreet(),
                command.getCity(),
                command.getState(),
                command.getZipCode(),
                command.getCountry()
        );

        // Domain service kullanarak adres güncelle
        customerDomainService.updateAddress(customer, shippingAddress);
        customerRepository.save(customer);


        return cart;
    }
}

