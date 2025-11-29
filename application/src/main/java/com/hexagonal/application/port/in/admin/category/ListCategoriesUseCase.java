package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.domain.entity.Category;

import java.util.List;

public interface ListCategoriesUseCase {
    List<Category> execute();
}

