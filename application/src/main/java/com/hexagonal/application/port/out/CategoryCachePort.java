package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryCachePort {
    Optional<List<Category>> getAll();
    void putAll(List<Category> categories);
    void invalidateAll();

    // Per-category (by id) operations
    Optional<Category> getById(String categoryId);
    void putById(String categoryId, Category category);
    void invalidateById(String categoryId);
}
