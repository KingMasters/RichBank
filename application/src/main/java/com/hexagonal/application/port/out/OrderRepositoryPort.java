package com.hexagonal.application.port.out;

import com.hexagonal.entity.Order;
import com.hexagonal.vo.ID;

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

