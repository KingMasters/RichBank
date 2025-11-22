package com.hexagonal.application.usecase.admin.category;

import com.hexagonal.application.port.in.admin.category.AssignProductsToCategoryInputPort;
import com.hexagonal.application.port.out.CategoryRepositoryOutputPort;
import com.hexagonal.application.port.out.ProductRepositoryOutputPort;
import com.hexagonal.entity.Category;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;

import java.util.Set;

public class AssignProductsToCategoryUseCase implements AssignProductsToCategoryInputPort {
    private final CategoryRepositoryOutputPort categoryRepository;
    private final ProductRepositoryOutputPort productRepository;

    public AssignProductsToCategoryUseCase(CategoryRepositoryOutputPort categoryRepository, ProductRepositoryOutputPort productRepository) {
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