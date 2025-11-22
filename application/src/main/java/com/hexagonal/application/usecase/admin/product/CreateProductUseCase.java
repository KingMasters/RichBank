package com.hexagonal.application.usecase.admin.product;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.port.in.admin.product.CreateProductInputPort;
import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Product;
import com.hexagonal.exception.DuplicateEntityException;
import com.hexagonal.vo.Dimensions;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import com.hexagonal.vo.Weight;

import java.util.Set;

/**
 * Input Port - Create Product Use Case Implementation
 * Framework katmanından (Controller) application katmanına giriş noktası
 */
public class CreateProductUseCase implements CreateProductInputPort {
    private final ProductRepositoryOutputPort productRepository;

    public CreateProductUseCase(ProductRepositoryOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(CreateProductCommand command) {
        // Check if SKU already exists
        if (productRepository.existsBySku(command.getSku())) {
            throw new DuplicateEntityException("Product", "SKU", command.getSku());
        }

        // Create product
        Money price = Money.of(command.getPrice(), command.getCurrency());
        Product product = Product.create(command.getName(), price, command.getSku());

        // Set description if provided
        if (command.getDescription() != null && !command.getDescription().isBlank()) {
            product.updateDescription(command.getDescription());
        }

        // Add categories if provided
        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            Set<ID> categoryIds = command.getCategoryIds().stream()
                    .map(ID::of)
                    .collect(java.util.stream.Collectors.toSet());
            product.addCategories(categoryIds);
        }

        // Add images if provided
        if (command.getImages() != null) {
            command.getImages().forEach(product::addImage);
        }

        // Set weight if provided
        if (command.getWeight() != null && command.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(command.getWeightUnit().toUpperCase());
            Weight weight = Weight.of(command.getWeight(), weightUnit);
            product.setWeight(weight);
        }

        // Set dimensions if provided
        if (command.getLength() != null && command.getWidth() != null && 
            command.getHeight() != null && command.getDimensionUnit() != null) {
            Dimensions.LengthUnit dimensionUnit = Dimensions.LengthUnit.valueOf(command.getDimensionUnit().toUpperCase());
            Dimensions dimensions = Dimensions.of(command.getLength(), command.getWidth(), command.getHeight(), dimensionUnit);
            product.setDimensions(dimensions);
        }

        return productRepository.save(product);
    }
}

