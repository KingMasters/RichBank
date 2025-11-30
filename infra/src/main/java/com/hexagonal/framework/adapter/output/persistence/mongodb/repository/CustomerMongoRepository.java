package com.hexagonal.framework.adapter.output.persistence.mongodb.repository;

import com.hexagonal.framework.adapter.output.persistence.mongodb.document.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerMongoRepository extends MongoRepository<CustomerDocument, UUID> {
    Optional<CustomerDocument> findByEmail(String email);
    boolean existsByEmail(String email);
}

