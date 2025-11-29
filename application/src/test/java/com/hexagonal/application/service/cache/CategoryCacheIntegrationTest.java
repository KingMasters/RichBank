package com.hexagonal.application.service.cache;

import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.application.port.out.CategoryRepositoryPort;
import com.hexagonal.application.service.query.admin.category.ListCategoriesService;
import com.hexagonal.domain.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CategoryCacheIntegrationTest {

    private CategoryRepositoryPort categoryRepository;
    private CategoryCachePort categoryCache;
    private ListCategoriesService listCategoriesService;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepositoryPort.class);
        // use a lightweight in-memory test cache adapter (no framework dependency)
        categoryCache = new InMemoryCategoryCacheAdapter();
        listCategoriesService = new ListCategoriesService(categoryCache, categoryRepository);
    }

    @Test
    void cacheShouldBePopulatedAndUsedOnSecondCall() {
        Category c1 = Category.create("cat1");
        Category c2 = Category.create("cat2");
        List<Category> categories = List.of(c1, c2);

        when(categoryRepository.findAll()).thenReturn(categories);

        // first call: should hit repository
        List<Category> first = listCategoriesService.execute();
        assertThat(first).hasSize(2);
        verify(categoryRepository, times(1)).findAll();

        // second call: should come from cache, repository not called again
        List<Category> second = listCategoriesService.execute();
        assertThat(second).hasSize(2);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void readAfterWritePutByIdShouldReturnNewCategoryFromCache() {
        // create a category and put into cache
        Category created = Category.create("new-cat");
        categoryCache.putById(created.getId().toString(), created);

        // now getById should return it
        assertThat(categoryCache.getById(created.getId().toString())).isPresent().get().isEqualTo(created);
    }

    // Simple in-memory test adapter that implements CategoryCachePort without external dependencies
    private static class InMemoryCategoryCacheAdapter implements CategoryCachePort {
        private final Map<String, Category> byId = new HashMap<>();
        private List<Category> all = null;

        @Override
        public Optional<List<Category>> getAll() {
            return all == null ? Optional.empty() : Optional.of(List.copyOf(all));
        }

        @Override
        public void putAll(List<Category> categories) {
            if (categories == null) return;
            this.all = new ArrayList<>(categories);
            // populate per-id map
            byId.clear();
            for (Category c : categories) {
                byId.put(c.getId().toString(), c);
            }
        }

        @Override
        public void invalidateAll() {
            this.all = null;
            byId.clear();
        }

        @Override
        public Optional<Category> getById(String categoryId) {
            if (categoryId == null) return Optional.empty();
            Category c = byId.get(categoryId);
            // if not present, try to find in 'all'
            if (c == null && all != null) {
                for (Category cat : all) {
                    if (categoryId.equals(cat.getId().toString())) {
                        c = cat;
                        byId.put(categoryId, c);
                        break;
                    }
                }
            }
            return Optional.ofNullable(c);
        }

        @Override
        public void putById(String categoryId, Category category) {
            if (categoryId == null || category == null) return;
            byId.put(categoryId, category);
            // also update all list if present
            if (all != null) {
                boolean updated = false;
                List<Category> copy = new ArrayList<>(all.size());
                for (Category c : all) {
                    if (categoryId.equals(c.getId().toString())) {
                        copy.add(category);
                        updated = true;
                    } else {
                        copy.add(c);
                    }
                }
                if (!updated) copy.add(category);
                this.all = copy;
            }
        }

        @Override
        public void invalidateById(String categoryId) {
            if (categoryId == null) return;
            byId.remove(categoryId);
            if (all != null) {
                List<Category> filtered = new ArrayList<>();
                for (Category c : all) {
                    if (!categoryId.equals(c.getId().toString())) filtered.add(c);
                }
                this.all = filtered;
            }
        }
    }
}
