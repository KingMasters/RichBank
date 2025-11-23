package com.hexagonal.application.usecase.admin.product;

import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.port.in.admin.product.UpdateProductUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.EntityNotFoundException;
import com.hexagonal.vo.Dimensions;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.Weight;

import java.util.Set;

/**
 * Input Port - Update Product Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
public class UpdateProductUseCaseHandler implements UpdateProductUseCase {
    private final ProductRepositoryPort productRepository;

    public UpdateProductUseCaseHandler(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UpdateProductCommand command) {
        ID productId = ID.of(command.getProductId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        // Update name if provided
        if (command.getName() != null && !command.getName().isBlank()) {
            product.updateName(command.getName());
        }

        // Update description
        if (command.getDescription() != null) {
            product.updateDescription(command.getDescription());
        }

        // Update price if provided
        if (command.getPrice() != null && command.getCurrency() != null) {
            Money newPrice = Money.of(command.getPrice(), command.getCurrency());
            product.updatePrice(newPrice);
        }

        // Update categories if provided
        if (command.getCategoryIds() != null) {
            product.clearCategories();
            if (!command.getCategoryIds().isEmpty()) {
                Set<ID> categoryIds = command.getCategoryIds().stream()
                        .map(ID::of)
                        .collect(java.util.stream.Collectors.toSet());
                product.addCategories(categoryIds);
            }
        }

        // Update images if provided
        if (command.getImages() != null) {
            // Remove all existing images
            product.getImages().forEach(product::removeImage);
            // Add new images
            command.getImages().forEach(product::addImage);
        }

        // Update weight if provided
        if (command.getWeight() != null && command.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(command.getWeightUnit().toUpperCase());
            Weight weight = Weight.of(command.getWeight(), weightUnit);
            product.setWeight(weight);
        } else if (command.getWeight() == null) {
            product.removeWeight();
        }

        // Update dimensions if provided
        if (command.getLength() != null && command.getWidth() != null && 
            command.getHeight() != null && command.getDimensionUnit() != null) {
            Dimensions.LengthUnit dimensionUnit = Dimensions.LengthUnit.valueOf(command.getDimensionUnit().toUpperCase());
            Dimensions dimensions = Dimensions.of(command.getLength(), command.getWidth(), command.getHeight(), dimensionUnit);
            product.setDimensions(dimensions);
        } else if (command.getLength() == null && command.getWidth() == null && command.getHeight() == null) {
            product.removeDimensions();
        }

        return productRepository.save(product);
    }
}

