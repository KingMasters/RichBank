package com.hexagonal.application.ports.input.admin.category;

import com.hexagonal.application.ports.output.CategoryRepositoryOutputPort;
import com.hexagonal.application.ports.output.ProductRepositoryOutputPort;
import com.hexagonal.application.usecases.admin.category.AssignProductsToCategoryUseCase;
import com.hexagonal.entity.Category;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.Set;

public class AssignProductsToCategoryInputPort implements AssignProductsToCategoryUseCase {
    private final CategoryRepositoryOutputPort categoryRepository;
    private final ProductRepositoryOutputPort productRepository;

    public AssignProductsToCategoryInputPort(CategoryRepositoryOutputPort categoryRepository, ProductRepositoryOutputPort productRepository) {
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