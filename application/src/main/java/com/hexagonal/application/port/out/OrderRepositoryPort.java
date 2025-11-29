package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(ID id);
    List<Order> findAll();
    List<Order> findByCustomerId(ID customerId);
    void deleteById(ID id);
    boolean existsById(ID id);
}

