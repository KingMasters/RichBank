package com.hexagonal.domain.repository;

import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(ID id);
    List<Category> findAll();
    List<Category> findRootCategories();
    List<Category> findByParentCategoryId(ID parentCategoryId);
    void deleteById(ID id);
    boolean existsById(ID id);
}

