package com.hexagonal.application.usecases.admin.product;

public interface RemoveProductUseCase {
    void execute(String productId);
    void executeHardDelete(String productId);
}

