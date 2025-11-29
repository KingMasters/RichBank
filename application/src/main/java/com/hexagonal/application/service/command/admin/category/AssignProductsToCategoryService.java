package com.hexagonal.application.service.command.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.category.AssignProductsToCategoryUseCase;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.ProductDomainService;
import com.hexagonal.domain.vo.ID;

import java.util.Set;

/**
 * Application Service - AssignProductsToCategory Use Case
 *
 * Orkestrasyon Servisi:
 * - ProductDomainService'i kullanarak domain logic'i çalıştırır
 * - Repository'lerden sorumludur (Port adapter)
 * - Üretilen domain olaylarını işler
 */
@UseCase
public class AssignProductsToCategoryService implements AssignProductsToCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final ProductRepositoryPort productRepository;
    private final ProductDomainService productDomainService;

    public AssignProductsToCategoryService(CategoryRepositoryPort categoryRepository,
                                           ProductRepositoryPort productRepository,
                                           ProductDomainService productDomainService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    /**
     * Müşteri use case'i: Kategoriye ürünler ata
     * 1. Kategori mevcudiyetini kontrol et
     * 2. Her ürün için domain servisi çağır (statefulness)
     * 3. Güncellenmiş ürünleri repository'ye kaydet
     */
    @Override
    public void execute(ID categoryId, java.util.Set<ID> productIds) {
        // Kategori mevcudiyeti kontrol et
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));

        // Her ürüne kategori ekle
        for (ID productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            // Domain servisi çağır: Bu sadece domain logicini çalıştırır
            productDomainService.assignCategoriesToProduct(product, java.util.Set.of(categoryId));

            // Güncellenmiş ürünü kaydet
            productRepository.save(product);
        }
    }
}