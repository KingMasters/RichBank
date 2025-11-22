package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.application.dto.CreateCategoryCommand;
import com.hexagonal.entity.Category;

public interface CreateCategoryInputPort {
    Category execute(CreateCategoryCommand command);
}