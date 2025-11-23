package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.entity.Category;

public interface CreateCategoryUseCase {
    Category execute(CreateCategoryCommand command);
}