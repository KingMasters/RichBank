package com.hexagonal.framework.adapter.output.persistence.mongodb;

import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.mongodb.mapper.CustomerDocumentMapper;
import com.hexagonal.framework.adapter.output.persistence.mongodb.repository.CustomerMongoRepository;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class CustomerMongoAdapter implements CustomerRepositoryOutputPort {
    
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
}

