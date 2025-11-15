package com.hexagonal.application.ports.input.admin.category;

import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.application.ports.output.CategoryRepositoryOutputPort;
import com.hexagonal.application.usecases.admin.category.CreateCategoryUseCase;
import com.hexagonal.entity.Category;
import com.hexagonal.vo.ID;

public class CreateCategoryInputPort implements CreateCategoryUseCase {
    private final CategoryRepositoryOutputPort categoryRepository;

    public CreateCategoryInputPort(CategoryRepositoryOutputPort categoryRepository) {
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