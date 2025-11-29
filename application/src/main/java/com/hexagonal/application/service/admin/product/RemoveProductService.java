package com.hexagonal.application.service.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.product.RemoveProductUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

/**
 * Input Port - Remove Product Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
@UseCase
public class RemoveProductService implements RemoveProductUseCase {
    private final ProductRepositoryPort productRepository;

    public RemoveProductService(ProductRepositoryPort productRepository) {
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

