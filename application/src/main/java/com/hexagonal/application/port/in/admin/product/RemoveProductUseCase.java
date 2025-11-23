package com.hexagonal.application.port.in.admin.product;

public interface RemoveProductUseCase {
    void execute(String productId);
    void executeHardDelete(String productId);
}

