package com.hexagonal.framework.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.domain.entity.Category;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Caffeine-backed cache adapter for categories.
 * Maintains a full-list cache and per-id entries.
 */
public class CaffeineCategoryCacheAdapter implements CategoryCachePort {
    private static final String KEY_ALL = "categories:all";

    private final Cache<String, List<Category>> allCache;
    private final Cache<String, Category> byIdCache;

    public CaffeineCategoryCacheAdapter(long ttlSeconds, long maximumSize) {
        this.allCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maximumSize)
                .build();

        this.byIdCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maximumSize * 2)
                .build();
    }

    @Override
    public Optional<List<Category>> getAll() {
        return Optional.ofNullable(allCache.getIfPresent(KEY_ALL));
    }

    @Override
    public void putAll(List<Category> categories) {
        if (categories == null) return;
        List<Category> copy = List.copyOf(categories);
        allCache.put(KEY_ALL, copy);
        // populate per-id cache as well
        copy.forEach(cat -> byIdCache.put(cat.getId().toString(), cat));
    }

    @Override
    public void invalidateAll() {
        allCache.invalidate(KEY_ALL);
        byIdCache.asMap().keySet().forEach(byIdCache::invalidate);
    }

    @Override
    public Optional<Category> getById(String categoryId) {
        if (categoryId == null) return Optional.empty();
        Category c = byIdCache.getIfPresent(categoryId);
        // if not present, and we have full list, try to find
        if (c == null) {
            List<Category> all = allCache.getIfPresent(KEY_ALL);
            if (all != null) {
                c = all.stream().filter(cat -> categoryId.equals(cat.getId().toString())).findFirst().orElse(null);
                if (c != null) byIdCache.put(categoryId, c);
            }
        }
        return Optional.ofNullable(c);
    }

    @Override
    public void putById(String categoryId, Category category) {
        if (categoryId == null || category == null) return;
        byIdCache.put(categoryId, category);
        // also update all cache if present
        List<Category> all = allCache.getIfPresent(KEY_ALL);
        if (all != null) {
            List<Category> updated = all.stream()
                    .map(c -> c.getId().toString().equals(categoryId) ? category : c)
                    .collect(Collectors.toList());
            allCache.put(KEY_ALL, List.copyOf(updated));
        }
    }

    @Override
    public void invalidateById(String categoryId) {
        if (categoryId == null) return;
        byIdCache.invalidate(categoryId);
        // Also remove from all cache if present
        List<Category> all = allCache.getIfPresent(KEY_ALL);
        if (all != null) {
            List<Category> filtered = all.stream().filter(c -> !categoryId.equals(c.getId().toString())).collect(Collectors.toList());
            allCache.put(KEY_ALL, List.copyOf(filtered));
        }
    }
}
