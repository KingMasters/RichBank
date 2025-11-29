package com.hexagonal.application.service.command.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.port.in.admin.product.UpdateProductUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.EntityNotFoundException;
import com.hexagonal.domain.service.ProductDomainService;
import com.hexagonal.domain.vo.Dimensions;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.Weight;

import java.util.Set;

/**
 * Application Service - Update Product Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - Repository'den ürün alır
 * - ProductDomainService kullanarak ürün özelliklerini günceller
 * - Güncellenmiş ürünü repository'ye kaydeder
 */
@UseCase
public class UpdateProductService implements UpdateProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductDomainService productDomainService;

    public UpdateProductService(ProductRepositoryPort productRepository,
                                ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    /**
     * Ürün güncelleme use case'i
     * 1. Ürünü repository'den al
     * 2. Domain service kullanarak ürün özelliklerini güncelle
     * 3. Güncellenmiş ürünü kaydet
     */
    @Override
    public Product execute(UpdateProductCommand command) {
        ID productId = ID.of(command.getProductId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        // Ürün adını güncelle
        if (command.getName() != null && !command.getName().isBlank()) {
            product.updateName(command.getName());
        }

        // Açıklamayı güncelle
        if (command.getDescription() != null) {
            productDomainService.updateDescription(product, command.getDescription());
        }

        // Fiyatı güncelle
        if (command.getPrice() != null && command.getCurrency() != null) {
            Money newPrice = Money.of(command.getPrice(), command.getCurrency());
            product.updatePrice(newPrice);
        }

        // Kategorileri güncelle
        if (command.getCategoryIds() != null) {
            product.clearCategories();
            if (!command.getCategoryIds().isEmpty()) {
                Set<ID> categoryIds = command.getCategoryIds().stream()
                        .map(ID::of)
                        .collect(java.util.stream.Collectors.toSet());
                productDomainService.assignCategoriesToProduct(product, categoryIds);
            }
        }

        // Resimleri güncelle
        if (command.getImages() != null) {
            product.getImages().forEach(product::removeImage);
            command.getImages().forEach(product::addImage);
        }

        // Ağırlığı güncelle
        if (command.getWeight() != null && command.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(command.getWeightUnit().toUpperCase());
            Weight weight = Weight.of(command.getWeight(), weightUnit);
            product.setWeight(weight);
        } else if (command.getWeight() == null) {
            product.removeWeight();
        }

        // Boyutları güncelle
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

