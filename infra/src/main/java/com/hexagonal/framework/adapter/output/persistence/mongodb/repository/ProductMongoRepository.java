package com.hexagonal.framework.adapter.output.persistence.mongodb.repository;

import com.hexagonal.framework.adapter.output.persistence.mongodb.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductMongoRepository extends MongoRepository<ProductDocument, UUID> {
    Optional<ProductDocument> findBySku(String sku);
    boolean existsBySku(String sku);
}

