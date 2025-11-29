package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

/**
 * Output Port - Application katmanının domain repository'sine erişim için port
 */
public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(ID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
    boolean existsBySku(String sku);
}

