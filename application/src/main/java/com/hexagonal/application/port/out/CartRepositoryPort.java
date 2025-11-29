package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.vo.ID;

import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(ID id);
    Optional<Cart> findByCustomerId(ID customerId);
    void deleteById(ID id);
    boolean existsById(ID id);
}

