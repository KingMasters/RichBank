package com.hexagonal.application.service.query.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.domain.entity.Category;

import java.util.List;

@UseCase
public class ListCategoriesService {
    private final CategoryCachePort categoryCache;
    private final CategoryRepositoryPort categoryRepository;

    public ListCategoriesService(CategoryCachePort categoryCache,
                                 CategoryRepositoryPort categoryRepository) {
        this.categoryCache = categoryCache;
        this.categoryRepository = categoryRepository;
    }

    public List<Category> execute() {
        // Check cache first
        return categoryCache.getAll()
                .orElseGet(() -> {
                    List<Category> categories = categoryRepository.findAll();
                    categoryCache.putAll(categories);
                    return categories;
                });
    }
}

