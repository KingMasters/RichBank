package com.hexagonal.application.service.command.customer.account;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.application.port.in.customer.account.UpdatePersonalInformationUseCase;
import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.CustomerDomainService;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Update Personal Information Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den müşteri alır
 * - CustomerDomainService'i çağırarak domain logic'i uygular
 * - Güncellenmiş müşteri bilgilerini repository'ye kaydeder
 */
@UseCase
public class UpdatePersonalInformationService implements UpdatePersonalInformationUseCase {
    private final CustomerRepositoryPort customerRepository;
    private final CustomerDomainService customerDomainService;

    public UpdatePersonalInformationService(CustomerRepositoryPort customerRepository,
                                            CustomerDomainService customerDomainService) {
        this.customerRepository = customerRepository;
        this.customerDomainService = customerDomainService;
    }

    /**
     * Müşteri bilgisi güncelleme use case'i
     * 1. Müşteri bilgilerini repository'den al
     * 2. Domain service'i çağırarak bilgileri güncelle
     * 3. Güncellenmiş müşteri bilgisini kaydet
     */
    @Override
    public Customer execute(UpdatePersonalInformationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("UpdatePersonalInformationCommand cannot be null");
        }

        ID customerId = ID.of(command.getCustomerId());
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        // Ad-soyadı güncelle
        if (command.getFirstName() != null || command.getLastName() != null) {
            String firstName = command.getFirstName() != null ? command.getFirstName() : customer.getFirstName();
            String lastName = command.getLastName() != null ? command.getLastName() : customer.getLastName();
            customerDomainService.updatePersonalInformation(customer, firstName, lastName);
        }

        // Email güncelle
        if (command.getEmail() != null && !command.getEmail().isBlank()) {
            Email email = Email.of(command.getEmail());
            customer.updateEmail(email);
        }

        // Telefon numarasını güncelle
        if (command.getPhoneNumber() != null) {
            customerDomainService.updatePhoneNumber(customer, command.getPhoneNumber());
        }

        return customerRepository.save(customer);
    }
}

