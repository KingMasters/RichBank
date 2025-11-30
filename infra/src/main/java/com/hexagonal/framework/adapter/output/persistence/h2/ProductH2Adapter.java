package com.hexagonal.framework.adapter.output.persistence.h2;

import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.framework.adapter.output.persistence.h2.mapper.ProductJpaMapper;
import com.hexagonal.framework.adapter.output.persistence.h2.repository.ProductJpaRepository;
import com.hexagonal.framework.adapter.output.persistence.PersistenceAdapter;
import com.hexagonal.domain.vo.ID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistenceAdapter
public class ProductH2Adapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository jpaRepository;
    
    public ProductH2Adapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        var entity = ProductJpaMapper.toEntity(product);
        var savedEntity = jpaRepository.save(entity);
        return ProductJpaMapper.toDomain(savedEntity);
    }
    
    @Override
    @Cacheable(value = "products", key = "#id.value.toString()")
    public Optional<Product> findById(ID id) {
        return jpaRepository.findById(id.getValue())
            .map(ProductJpaMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "products", key = "'sku:' + #sku")
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku)
            .map(ProductJpaMapper::toDomain);
    }
    
    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
            .map(ProductJpaMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteById(ID id) {
        jpaRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(ID id) {
        return jpaRepository.existsById(id.getValue());
    }
    
    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }
}

