package com.hexagonal.application.service.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.category.ModifyCategoryHierarchyUseCase;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ModifyCategoryHierarchyService implements ModifyCategoryHierarchyUseCase {
    private final CategoryRepositoryPort categoryRepository;

    public ModifyCategoryHierarchyService(CategoryRepositoryPort categoryRepository) {
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