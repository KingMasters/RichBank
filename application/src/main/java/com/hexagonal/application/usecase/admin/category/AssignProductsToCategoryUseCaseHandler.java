package com.hexagonal.application.usecase.admin.category;

import com.hexagonal.application.port.in.admin.category.AssignProductsToCategoryUseCase;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.application.port.out.ProductRepositoryPort;
import com.hexagonal.entity.Category;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.Set;

public class AssignProductsToCategoryUseCaseHandler implements AssignProductsToCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final ProductRepositoryPort productRepository;

    public AssignProductsToCategoryUseCaseHandler(CategoryRepositoryPort categoryRepository, ProductRepositoryPort productRepository) {
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