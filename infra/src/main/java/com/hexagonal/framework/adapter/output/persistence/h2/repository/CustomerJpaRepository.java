package com.hexagonal.framework.adapter.output.persistence.h2.repository;

import com.hexagonal.framework.adapter.output.persistence.h2.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

