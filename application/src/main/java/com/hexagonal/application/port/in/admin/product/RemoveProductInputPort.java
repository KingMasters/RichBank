package com.hexagonal.application.port.in.admin.product;

public interface RemoveProductInputPort {
    void execute(String productId);
    void executeHardDelete(String productId);
}

