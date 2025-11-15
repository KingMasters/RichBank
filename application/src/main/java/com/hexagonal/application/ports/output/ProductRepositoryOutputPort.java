package com.hexagonal.application.ports.output;

import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.List;
import java.util.Optional;

/**
 * Output Port - Application katmanının domain repository'sine erişim için port
 */
public interface ProductRepositoryOutputPort {
    Product save(Product product);
    Optional<Product> findById(ID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
    boolean existsBySku(String sku);
}

