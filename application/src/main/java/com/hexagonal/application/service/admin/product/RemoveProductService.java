package com.hexagonal.application.service.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.product.RemoveProductUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;

/**
 * Application Service - Remove Product Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Ürünü repository'den alır
 * - Soft delete yaparak ürünü silinmiş olarak işaretler
 * - Hard delete için ayrı bir metod sunar
 */
@UseCase
public class RemoveProductService implements RemoveProductUseCase {
    private final ProductRepositoryPort productRepository;

    public RemoveProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Ürün silme use case'i (Soft Delete)
     * - Ürünü silinmiş olarak işaretler
     * - Sipariş geçmişi korunur
     * - Veri bütünlüğü sağlanır
     */
    @Override
    public void execute(String productId) {
        ID id = ID.of(productId);
        
        // Ürün mevcudiyetini kontrol et
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));

        // Soft delete: silinmiş olarak işaretle
        product.markAsDiscontinued();
        productRepository.save(product);
    }

    /**
     * Ürün tamamen silme (Hard Delete)
     * - Veritabanından tamamen sil
     * - DİKKAT: Sipariş geçmişi için sorun yaratabilir
     */
    @Override
    public void executeHardDelete(String productId) {
        ID id = ID.of(productId);
        
        // Ürün mevcudiyetini kontrol et
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product", id);
        }

        // Hard delete - dikkatli kullan!
        productRepository.deleteById(id);
    }
}

