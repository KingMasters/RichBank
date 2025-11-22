package com.hexagonal.application.usecase.admin.product;

import com.hexagonal.application.port.in.admin.product.RemoveProductInputPort;
import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;

/**
 * Input Port - Remove Product Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
public class RemoveProductUseCase implements RemoveProductInputPort {
    private final ProductRepositoryOutputPort productRepository;

    public RemoveProductUseCase(ProductRepositoryOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void execute(String productId) {
        ID id = ID.of(productId);
        
        // Check if product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));

        // Mark as discontinued instead of hard delete (soft delete)
        // This preserves order history and maintains data integrity
        product.markAsDiscontinued();
        productRepository.save(product);
    }

    @Override
    public void executeHardDelete(String productId) {
        ID id = ID.of(productId);
        
        // Check if product exists
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product", id);
        }

        // Hard delete - use with caution
        productRepository.deleteById(id);
    }
}

