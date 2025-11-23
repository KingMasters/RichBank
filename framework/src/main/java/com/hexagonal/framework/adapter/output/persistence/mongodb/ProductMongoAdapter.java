package com.hexagonal.framework.adapter.output.persistence.mongodb;

import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Product;
import com.hexagonal.framework.adapter.output.persistence.mongodb.mapper.ProductDocumentMapper;
import com.hexagonal.framework.adapter.output.persistence.mongodb.repository.ProductMongoRepository;
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
public class ProductMongoAdapter implements ProductRepositoryPort {
    
    private final ProductMongoRepository mongoRepository;
    
    public ProductMongoAdapter(ProductMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        var document = ProductDocumentMapper.toDocument(product);
        var savedDocument = mongoRepository.save(document);
        return ProductDocumentMapper.toDomain(savedDocument);
    }
    
    @Override
    @Cacheable(value = "products", key = "#id.value.toString()")
    public Optional<Product> findById(ID id) {
        return mongoRepository.findById(id.getValue())
            .map(ProductDocumentMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "products", key = "'sku:' + #sku")
    public Optional<Product> findBySku(String sku) {
        return mongoRepository.findBySku(sku)
            .map(ProductDocumentMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<Product> findAll() {
        return mongoRepository.findAll().stream()
            .map(ProductDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteById(ID id) {
        mongoRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(ID id) {
        return mongoRepository.existsById(id.getValue());
    }
    
    @Override
    public boolean existsBySku(String sku) {
        return mongoRepository.existsBySku(sku);
    }
}

