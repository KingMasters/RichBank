package com.hexagonal.application.port.out;

import com.hexagonal.domain.entity.Category;
import com.hexagonal.domain.vo.ID;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(ID id);
    List<Category> findAll();
    List<Category> findByParentCategoryId(ID parentCategoryId);
    void deleteById(ID id);
    boolean existsById(ID id);
}