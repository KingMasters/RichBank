package com.hexagonal.framework.adapter.output.persistence.h2;

import com.hexagonal.application.ports.output.CustomerRepositoryOutputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.framework.adapter.output.persistence.h2.mapper.CustomerEntityMapper;
import com.hexagonal.framework.adapter.output.persistence.h2.repository.CustomerJpaRepository;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomerH2Adapter implements CustomerRepositoryOutputPort {
    
    private final CustomerJpaRepository jpaRepository;
    
    public CustomerH2Adapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public Customer save(Customer customer) {
        var entity = CustomerEntityMapper.toEntity(customer);
        var savedEntity = jpaRepository.save(entity);
        return CustomerEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    @Cacheable(value = "customers", key = "#id.value.toString()")
    public Optional<Customer> findById(ID id) {
        return jpaRepository.findById(id.getValue())
            .map(CustomerEntityMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "customers", key = "'email:' + #email.getValue()")
    public Optional<Customer> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
            .map(CustomerEntityMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "customers", key = "'all'")
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream()
            .map(CustomerEntityMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public void deleteById(ID id) {
        jpaRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(ID id) {
        return jpaRepository.existsById(id.getValue());
    }

    @Override
    public List<String> getPasswordHistory(ID id) {
        return jpaRepository.findById(id.getValue())
            .map(entity -> entity.getPasswordHistory() == null ? Collections.<String>emptyList() : entity.getPasswordHistory())
            .orElse(Collections.emptyList());
    }

    @Override
    public void updatePassword(ID id, String hashedPassword) {
        jpaRepository.findById(id.getValue()).ifPresent(entity -> {
            List<String> history = entity.getPasswordHistory();
            if (history == null) {
                history = new ArrayList<>();
            } else {
                history = new ArrayList<>(history);
            }

            // Prepend the new hash as most recent
            history.add(0, hashedPassword);
            entity.setPassword(hashedPassword);
            entity.setPasswordHistory(history);

            jpaRepository.save(entity);
        });
    }
}
