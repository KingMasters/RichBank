package com.hexagonal.application.ports.output;

import com.hexagonal.entity.Cart;
import com.hexagonal.vo.ID;

import java.util.Optional;

public interface CartRepositoryOutputPort {
    Cart save(Cart cart);
    Optional<Cart> findById(ID id);
    Optional<Cart> findByCustomerId(ID customerId);
    void deleteById(ID id);
    boolean existsById(ID id);
}

