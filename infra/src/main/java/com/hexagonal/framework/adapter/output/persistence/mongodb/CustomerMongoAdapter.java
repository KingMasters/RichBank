package com.hexagonal.framework.adapter.output.persistence.mongodb;

import com.hexagonal.application.port.out.CustomerRepositoryPort;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.mongodb.mapper.CustomerDocumentMapper;
import com.hexagonal.framework.adapter.output.persistence.mongodb.repository.CustomerMongoRepository;
import com.hexagonal.framework.adapter.output.persistence.PersistenceAdapter;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistenceAdapter
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class CustomerMongoAdapter implements CustomerRepositoryPort {
    
    private final CustomerMongoRepository mongoRepository;
    
    public CustomerMongoAdapter(CustomerMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public Customer save(Customer customer) {
        var document = CustomerDocumentMapper.toDocument(customer);
        var savedDocument = mongoRepository.save(document);
        return CustomerDocumentMapper.toDomain(savedDocument);
    }
    
    @Override
    @Cacheable(value = "customers", key = "#id.value.toString()")
    public Optional<Customer> findById(ID id) {
        return mongoRepository.findById(id.getValue())
            .map(CustomerDocumentMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "customers", key = "'email:' + #email.getValue()")
    public Optional<Customer> findByEmail(Email email) {
        return mongoRepository.findByEmail(email.getValue())
            .map(CustomerDocumentMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "customers", key = "'all'")
    public List<Customer> findAll() {
        return mongoRepository.findAll().stream()
            .map(CustomerDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public void deleteById(ID id) {
        mongoRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(ID id) {
        return mongoRepository.existsById(id.getValue());
    }

    @Override
    public List<String> getPasswordHistory(ID id) {
        return mongoRepository.findById(id.getValue())
            .map(doc -> doc.getPasswordHistory() == null ? Collections.<String>emptyList() : doc.getPasswordHistory())
            .orElse(Collections.emptyList());
    }

    @Override
    public void updatePassword(ID id, String hashedPassword) {
        // Load document, prepend new password to history (most recent first), set current password, save
        mongoRepository.findById(id.getValue()).ifPresent(doc -> {
            List<String> history = doc.getPasswordHistory();
            if (history == null) {
                history = new ArrayList<>();
            } else {
                // make a mutable copy
                history = new ArrayList<>(history);
            }

            // Prepend new hashed password
            history.add(0, hashedPassword);

            doc.setPassword(hashedPassword);
            doc.setPasswordHistory(history);

            mongoRepository.save(doc);
        });
    }
}
