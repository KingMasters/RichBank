package com.hexagonal.application.ports.input.admin.product;

import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.application.usecases.admin.product.ManageProductStockUseCase;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Quantity;

/**
 * Input Port - Manage Product Stock Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
public class ManageProductStockInputPort implements ManageProductStockUseCase {
    private final ProductRepositoryOutputPort productRepository;

    public ManageProductStockInputPort(ProductRepositoryOutputPort productRepository) {
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

