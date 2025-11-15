package com.hexagonal.repository;

import com.hexagonal.entity.Order;
import com.hexagonal.specification.Specification;
import com.hexagonal.vo.ID;

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

