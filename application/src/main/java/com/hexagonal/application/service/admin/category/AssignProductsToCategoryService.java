package com.hexagonal.application.service.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.category.AssignProductsToCategoryUseCase;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;

import java.util.Set;

@UseCase
public class AssignProductsToCategoryService implements AssignProductsToCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final ProductRepositoryPort productRepository;

    public AssignProductsToCategoryService(CategoryRepositoryPort categoryRepository, ProductRepositoryPort productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void execute(ID categoryId, Set<ID> productIds) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        for (ID productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.addCategory(categoryId);
            productRepository.save(product);
        }
    }
}