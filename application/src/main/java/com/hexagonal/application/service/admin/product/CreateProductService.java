package com.hexagonal.application.service.admin.product;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.port.in.admin.product.CreateProductUseCase;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.exception.DuplicateEntityException;
import com.hexagonal.domain.service.ProductDomainService;
import com.hexagonal.domain.vo.Dimensions;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.Money;
import com.hexagonal.domain.vo.Weight;

import java.util.Set;

/**
 * Application Service - Create Product Use Case Implementation
 *
 * Orkestrasyon Servisi:
 * - SKU benzersizliğini kontrol eder
 * - Product entity'sini oluşturur
 * - ProductDomainService'i çağırarak ürün özelliklerini ayarlar
 * - Repository'ye kaydeder
 */
@UseCase
public class CreateProductService implements CreateProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductDomainService productDomainService;

    public CreateProductService(ProductRepositoryPort productRepository,
                                ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    /**
     * Ürün oluşturma use case'i
     * 1. SKU'nun benzersiz olduğunu kontrol et
     * 2. Ürünü oluştur
     * 3. Domain service'i kullanarak ürün özelliklerini ayarla
     * 4. Repository'ye kaydet
     */
    @Override
    public Product execute(CreateProductCommand command) {
        // SKU'nun benzersiz olduğunu kontrol et
        if (productRepository.existsBySku(command.getSku())) {
            throw new DuplicateEntityException("Product", "SKU", command.getSku());
        }

        // Ürünü oluştur
        Money price = Money.of(command.getPrice(), command.getCurrency());
        Product product = Product.create(command.getName(), price, command.getSku());

        // Domain service'i çağırarak açıklamayı ayarla
        if (command.getDescription() != null && !command.getDescription().isBlank()) {
            productDomainService.updateDescription(product, command.getDescription());
        }

        // Kategorileri ekle
        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            java.util.Set<ID> categoryIds = command.getCategoryIds().stream()
                    .map(ID::of)
                    .collect(java.util.stream.Collectors.toSet());
            productDomainService.assignCategoriesToProduct(product, categoryIds);
        }

        // Resimleri ekle
        if (command.getImages() != null) {
            command.getImages().forEach(product::addImage);
        }

        // Ağırlığını ayarla
        if (command.getWeight() != null && command.getWeightUnit() != null) {
            Weight.WeightUnit weightUnit = Weight.WeightUnit.valueOf(command.getWeightUnit().toUpperCase());
            Weight weight = Weight.of(command.getWeight(), weightUnit);
            product.setWeight(weight);
        }

        // Boyutlarını ayarla
        if (command.getLength() != null && command.getWidth() != null &&
            command.getHeight() != null && command.getDimensionUnit() != null) {
            Dimensions.LengthUnit dimensionUnit = Dimensions.LengthUnit.valueOf(command.getDimensionUnit().toUpperCase());
            Dimensions dimensions = Dimensions.of(command.getLength(), command.getWidth(), command.getHeight(), dimensionUnit);
            product.setDimensions(dimensions);
        }

        return productRepository.save(product);
    }
}

