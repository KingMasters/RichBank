package com.hexagonal.application.ports.input.admin.category;

import com.hexagonal.application.ports.output.CategoryRepositoryOutputPort;
import com.hexagonal.application.usecases.admin.category.ModifyCategoryHierarchyUseCase;
import com.hexagonal.entity.Category;
import com.hexagonal.vo.ID;

public class ModifyCategoryHierarchyInputPort implements ModifyCategoryHierarchyUseCase {
    private final CategoryRepositoryOutputPort categoryRepository;

    public ModifyCategoryHierarchyInputPort(CategoryRepositoryOutputPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public void setParentCategory(ID categoryId, ID parentCategoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setParentCategory(parentCategoryId);
        categoryRepository.save(category);
    }

    @Override
    public void removeParentCategory(ID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.removeParentCategory();
        categoryRepository.save(category);
    }
}