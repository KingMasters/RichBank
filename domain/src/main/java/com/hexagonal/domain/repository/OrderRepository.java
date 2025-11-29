package com.hexagonal.domain.repository;

import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.specification.Specification;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(ID id);
    List<Order> findAll();
    List<Order> findByCustomerId(ID customerId);
    List<Order> findBySpecification(Specification<Order> specification);
    void deleteById(ID id);
    boolean existsById(ID id);
}

