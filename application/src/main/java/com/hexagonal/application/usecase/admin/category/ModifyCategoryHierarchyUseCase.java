package com.hexagonal.application.usecase.admin.category;

import com.hexagonal.application.port.out.CategoryRepositoryOutputPort;
import com.hexagonal.entity.Category;
import com.hexagonal.vo.ID;

public class ModifyCategoryHierarchyUseCase implements com.hexagonal.application.port.in.admin.category.ModifyCategoryHierarchyInputPort {
    private final CategoryRepositoryOutputPort categoryRepository;

    public ModifyCategoryHierarchyUseCase(CategoryRepositoryOutputPort categoryRepository) {
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