package com.hexagonal.domain.repository;

import com.hexagonal.domain.entity.Payment;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(ID id);
    List<Payment> findByOrderId(ID orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}

