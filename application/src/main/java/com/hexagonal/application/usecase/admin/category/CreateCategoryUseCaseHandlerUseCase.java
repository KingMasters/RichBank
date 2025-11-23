package com.hexagonal.application.usecase.admin.category;

import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.application.port.in.admin.category.CreateCategoryUseCase;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.entity.Category;
import com.hexagonal.vo.ID;

public class CreateCategoryUseCaseHandlerUseCase implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;

    public CreateCategoryUseCaseHandlerUseCase(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Category execute(CreateCategoryCommand command) {
        Category category = Category.create(command.getName());
        category.updateDescription(command.getDescription());
        if (command.getParentCategoryId() != null) {
            category.setParentCategory(ID.of(command.getParentCategoryId()));
        }
        return categoryRepository.save(category);
    }
}