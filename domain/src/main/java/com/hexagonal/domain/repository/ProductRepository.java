package com.hexagonal.domain.repository;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.specification.Specification;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(ID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    List<Product> findBySpecification(Specification<Product> specification);
    List<Product> findByCategoryId(ID categoryId);
    void deleteById(ID id);
    boolean existsById(ID id);
    boolean existsBySku(String sku);
}

