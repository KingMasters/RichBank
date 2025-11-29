package com.hexagonal.application.service.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.port.in.admin.product.ManageProductStockUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.ProductDomainService;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

/**
 * Application Service - Manage Product Stock Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den ürünü alır
 * - ProductDomainService çağırarak domain logicini uygular
 * - Güncellenmiş ürünü repository'ye kaydeder
 */
@UseCase
public class ManageProductStockService implements ManageProductStockUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductDomainService productDomainService;

    public ManageProductStockService(ProductRepositoryPort productRepository,
                                     ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    /**
     * Stok yönetimi use case'i
     * 1. Ürünü repository'den al
     * 2. Domain service'i çağırarak stok işlemini yap
     * 3. Güncellenmiş ürünü kaydet
     */
    @Override
    public Product execute(ManageStockCommand command) {
        ID productId = ID.of(command.getProductId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        Quantity quantity = Quantity.of(command.getQuantity());

        // Domain service'i çağır
        switch (command.getOperation()) {
            case ADD:
                productDomainService.addStock(product, quantity);
                break;
            case REMOVE:
                productDomainService.removeStock(product, quantity);
                break;
            case SET:
                productDomainService.setStock(product, quantity);
                break;
            default:
                throw new IllegalArgumentException("Unknown stock operation: " + command.getOperation());
        }

        return productRepository.save(product);
    }
}

