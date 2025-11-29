package com.hexagonal.application.service.query.admin.category;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.category.ListCategoriesUseCase;
import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.domain.entity.Category;

import java.util.List;

/**
 * Query Service - List All Categories Use Case Implementation
 *
 * Query Service:
 * - Cache'den kategorileri alır
 * - Cache miss durumunda repository'den alır
 * - Cache'e koyar
 */
@UseCase
public class ListCategoriesService implements ListCategoriesUseCase {
    private final CategoryCachePort categoryCache;
    private final CategoryRepositoryPort categoryRepository;

    public ListCategoriesService(CategoryCachePort categoryCache,
                                 CategoryRepositoryPort categoryRepository) {
        this.categoryCache = categoryCache;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Tüm kategorileri listeleme query'si
     * 1. Cache'den al
     * 2. Cache miss ise repository'den al
     * 3. Cache'e koy
     */
    @Override
    public List<Category> execute() {
        // Cache'de varsa döndür
        return categoryCache.getAll()
                .orElseGet(() -> {
                    // Cache miss: repository'den al
                    List<Category> categories = categoryRepository.findAll();
                    categoryCache.putAll(categories);
                    return categories;
                });
    }
}
