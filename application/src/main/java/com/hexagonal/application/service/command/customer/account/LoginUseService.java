package com.hexagonal.application.service.command.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.port.in.customer.account.LoginUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.Email;

/**
 * Application Service - Login Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - CustomerRepositoryPort'u çağırarak müşteri bilgisini alır
 * - Email ve şifre doğrulamasını yapar
 * - Müşteri durum kontrolü yapar
 */
@UseCase
public class LoginUseService implements LoginUseCase {
    private final CustomerRepositoryPort customerRepository;

    public LoginUseService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Müşteri giriş use case'i
     * 1. Email ile müşteri ara
     * 2. Müşteri durumunu kontrol et
     * 3. Müşteri bilgisini döndür
     *
     * NOT: Şifre doğrulaması gerçek uygulamada authentication service'i tarafından yapılmalıdır
     */
    @Override
    public Customer execute(LoginCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("LoginCommand cannot be null");
        }

        Email email = Email.of(command.getEmail());
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Customer with email " + command.getEmail() + " not found"));

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is not active");
        }

        // NOT: Şifre doğrulaması normalde authentication service tarafından yapılır
        // Bu örnek basitleştirilmiş bir versiyondur

        return customer;
    }
}

