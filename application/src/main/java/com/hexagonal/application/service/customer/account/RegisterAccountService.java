package com.hexagonal.application.service.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.port.in.customer.account.RegisterAccountUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.DuplicateEntityException;
import com.hexagonal.domain.vo.Email;

/**
 * Application Service - Register Account Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Email benzersizliğini kontrol eder
 * - Customer entity'sini oluşturur
 * - Repository'ye kaydeder
 */
@UseCase
public class RegisterAccountService implements RegisterAccountUseCase {
    private final CustomerRepositoryPort customerRepository;

    public RegisterAccountService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Hesap kayıt use case'i
     * 1. Email'in benzersiz olduğunu kontrol et
     * 2. Yeni müşteri entity'sini oluştur
     * 3. Telefon numarasını ayarla
     * 4. Repository'ye kaydet
     */
    @Override
    public Customer execute(RegisterAccountCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("RegisterAccountCommand cannot be null");
        }

        Email email = Email.of(command.getEmail());

        // Email'in benzersiz olduğunu kontrol et
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEntityException("Customer", "email", command.getEmail());
        }

        // Yeni müşteri oluştur
        Customer customer = Customer.create(
                command.getFirstName(),
                command.getLastName(),
                email
        );

        // Telefon numarasını ayarla
        if (command.getPhoneNumber() != null && !command.getPhoneNumber().isBlank()) {
            customer.updatePhoneNumber(command.getPhoneNumber());
        }

        return customerRepository.save(customer);
    }
}

