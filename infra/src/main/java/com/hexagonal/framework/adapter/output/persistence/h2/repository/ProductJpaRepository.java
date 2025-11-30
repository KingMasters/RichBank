package com.hexagonal.framework.adapter.output.persistence.h2.repository;

import com.hexagonal.framework.adapter.output.persistence.h2.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);
    boolean existsBySku(String sku);
}

