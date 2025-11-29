package com.hexagonal.application.service.command.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.application.port.in.admin.category.CreateCategoryUseCase;
import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.vo.ID;

@UseCase
public class CreateCategoryService implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final CategoryCachePort categoryCache;

    public CreateCategoryService(CategoryRepositoryPort categoryRepository, CategoryCachePort categoryCache) {
        this.categoryRepository = categoryRepository;
        this.categoryCache = categoryCache;
    }
    @Override
    public Category execute(CreateCategoryCommand command) {
        Category category = Category.create(command.getName());
        category.updateDescription(command.getDescription());
        if (command.getParentCategoryId() != null) {
            category.setParentCategory(ID.of(command.getParentCategoryId()));
        }
        Category saved = categoryRepository.save(category);
        // read-after-write: put the created category into per-id cache and update all-list cache
        categoryCache.putById(saved.getId().toString(), saved);
        return saved;
    }
}