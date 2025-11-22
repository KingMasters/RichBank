package com.hexagonal.application.usecase.admin.category;

import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.application.port.in.admin.category.CreateCategoryInputPort;
import com.hexagonal.application.port.out.CategoryRepositoryOutputPort;
import com.hexagonal.entity.Category;
import com.hexagonal.vo.ID;

public class CreateCategoryUseCase implements CreateCategoryInputPort {
    private final CategoryRepositoryOutputPort categoryRepository;

    public CreateCategoryUseCase(CategoryRepositoryOutputPort categoryRepository) {
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