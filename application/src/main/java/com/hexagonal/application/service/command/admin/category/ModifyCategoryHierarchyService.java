package com.hexagonal.application.service.command.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.category.ModifyCategoryHierarchyUseCase;
import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.vo.ID;

@UseCase
public class ModifyCategoryHierarchyService implements ModifyCategoryHierarchyUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final CategoryCachePort categoryCache;

    public ModifyCategoryHierarchyService(CategoryRepositoryPort categoryRepository, CategoryCachePort categoryCache) {
        this.categoryRepository = categoryRepository;
        this.categoryCache = categoryCache;
    }
    @Override
    public void setParentCategory(ID categoryId, ID parentCategoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setParentCategory(parentCategoryId);
        categoryRepository.save(category);
        categoryCache.invalidateById(categoryId.toString());
    }

    @Override
    public void removeParentCategory(ID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.removeParentCategory();
        categoryRepository.save(category);
        categoryCache.invalidateById(categoryId.toString());
    }
}