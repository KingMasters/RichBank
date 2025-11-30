package com.hexagonal.framework.adapter.output.persistence.h2.repository;

import com.hexagonal.framework.adapter.output.persistence.h2.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
    Optional<ProductJpaEntity> findBySku(String sku);
    boolean existsBySku(String sku);
}

