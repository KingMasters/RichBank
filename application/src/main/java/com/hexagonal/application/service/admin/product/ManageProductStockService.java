package com.hexagonal.application.service.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.port.in.admin.product.ManageProductStockUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Quantity;

/**
 * Input Port - Manage Product Stock Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
@UseCase
public class ManageProductStockService implements ManageProductStockUseCase {
    private final ProductRepositoryPort productRepository;

    public ManageProductStockService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(ManageStockCommand command) {
        ID productId = ID.of(command.getProductId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        Quantity quantity = Quantity.of(command.getQuantity());

        switch (command.getOperation()) {
            case ADD:
                product.addStock(quantity);
                break;
            case REMOVE:
                product.removeStock(quantity);
                break;
            case SET:
                product.setStock(quantity);
                break;
            default:
                throw new IllegalArgumentException("Unknown stock operation: " + command.getOperation());
        }

        return productRepository.save(product);
    }
}

