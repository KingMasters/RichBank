package com.hexagonal.application.usecase.admin.product;

import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.port.in.admin.product.ManageProductStockUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Quantity;

/**
 * Input Port - Manage Product Stock Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
public class ManageProductStockUseCaseHandler implements ManageProductStockUseCase {
    private final ProductRepositoryPort productRepository;

    public ManageProductStockUseCaseHandler(ProductRepositoryPort productRepository) {
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

